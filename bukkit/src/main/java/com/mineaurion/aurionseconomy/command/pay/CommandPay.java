package com.mineaurion.aurionseconomy.command.pay;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.mineaurion.aurionseconomy.Cause;
import com.mineaurion.aurionseconomy.LogInfo;
import com.mineaurion.aurionseconomy.Main;
import com.mineaurion.aurionseconomy.Mysql.MySQLEngine;

public class CommandPay implements CommandExecutor {

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("pay")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (player.hasPermission("economie.pay")) {
					if (args.length == 2) {
						if(Bukkit.getServer().getPlayer(args[0]) == null){
							Main.sendmessage("{{RED}}"+args[0]+" n'est pas connecter", sender.getName());
							return true;
						}
						
						Player recipient = Bukkit.getPlayer(args[0]);
						if(player.getUniqueId()==recipient.getUniqueId()){
							Main.sendmessage("{{RED}}tu ne peux pas te payer toi même", sender.getName());
							return true;
						}
						String amount = args[1];
						Pattern amountPattern = Pattern.compile("^[+]?(\\d*\\.)?\\d+$");
						Matcher m = amountPattern.matcher(amount);
						if (m.matches() && Double.parseDouble(amount) > 0) {
							String Playerbal = Main.getInstance().format(Double.parseDouble(amount));
							double balance = MySQLEngine.getBalance(player.getUniqueId().toString(),false);
							if (balance > Double.parseDouble(amount)) {
								double newbalanceplayer = balance - Double.parseDouble(amount);
								double balancerecipient = MySQLEngine.getBalance(recipient.getUniqueId().toString(),false);
								double newbalancerecipient = balancerecipient + Double.parseDouble(amount);
								
								boolean result = MySQLEngine.setBalance(player.getUniqueId().toString(), newbalanceplayer);
								boolean result2 = MySQLEngine.setBalance(recipient.getUniqueId().toString(), newbalancerecipient);
								
								Main.getInstance().getVaultconnector().withdrawPlayer(player, Double.parseDouble(amount));
								Main.getInstance().getVaultconnector().depositPlayer(recipient, Double.parseDouble(amount));
								
								if(result&&result2){
									DateTime dateTime = DateTime.now(DateTimeZone.forID("Europe/Paris"));
									Main.getInstance().writeLog(player.getName()+"->"+recipient.getName(), LogInfo.PAY, Cause.VAULT, dateTime,
											Double.parseDouble(amount));
									Main.sendmessage(
											"Tu as donné {{RED}}" + Playerbal + "{{WHITE}} à {{YELLOW}}" + recipient.getName(),
											sender.getName());
									Main.sendmessage(
											"Tu as reçu {{RED}}" + Playerbal + "{{WHITE}} de {{YELLOW}}" + player.getName(),
											player.getName());
									return true;
								}else {
									Main.sendmessage("{{RED}}une erreur est survenu", sender.getName());
									return true;
								}
							} else {
								Main.sendmessage("{{RED}}You don't have Money", sender.getName());
								return true;
							}
						} else {
							Main.sendmessage("{{RED}}Montant invalible ou <0", sender.getName());
							return true;
						}
					} else {
						Main.sendmessage("{{RED}}Use /pay <player> <montant>", sender.getName());
						return true;
					}
				} else {
					Main.sendmessage("{{RED}}You don't have permission", sender.getName());
					return true;
				}
			} else {
				Main.sendmessage("{{RED}}Tu n'est pas un joueurs utilise /money give", sender.getName());
				return true;
			}
		}
		return false;
	}
}
