package common;

import game.GameServer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.util.ArrayList;

import common.Consolex;
import common.Consolex.ConsoleColorEnum;
import realm.RealmServer;

public class Ancestra {

	private static final String CONFIG_FILE = "CyonConfig.txt";
	public static final short CONFIG_MAP_ATELIER = 15004;
	public static final int CONFIG_CELL_ATELIER = 224;
	public static final short CONFIG_SHOP_MAP = 15005;
	public static final int CONFIG_CELL_CELL = 394;
	public static String IP = "127.0.0.1";
	public static boolean isInit = false;
	public static String DB_HOST;
	public static String DB_USER;
	public static String DB_PASS;
	public static String STATIC_DB_NAME;
	public static String OTHER_DB_NAME;

	public static String GAMESERVER_IP;
	public static String CONFIG_MOTD = "";
	public static String CONFIG_MOTD_COLOR = "";
	public static boolean CONFIG_DEBUG = false;
	public static PrintStream PS;
	public static boolean CONFIG_POLICY = false;
	public static int CONFIG_REALM_PORT = 443;
	public static int CONFIG_GAME_PORT = 5555;

	public static boolean CONFIG_ALLOW_MULTI = false;
	public static int CONFIG_START_LEVEL = 1;
	public static int CONFIG_START_KAMAS = 0;
	public static String START_ITEMS = "";

	public static long INCARNAM_TIME = 30000;
	public static int CONFIG_SAVE_TIME = 10 * 60 * 10000;
	public static int CONFIG_LOAD_DELAY = 60000;
	public static int CONFIG_DROP = 1;
	public static boolean CONFIG_ZAAP = false;
	public static int CONFIG_PLAYER_LIMIT = 30;
	public static boolean CONFIG_IP_LOOPBACK = true;

	public static int XP_PVP = 10;
	public static int LVL_PVP = 15;
	public static boolean ALLOW_MULE_PVP = false;
	public static int XP_PVM = 1;
	public static int KAMAS = 1;
	public static int HONOR = 1;
	public static int XP_METIER = 1;
	public static int PORC_FM = 1;
	public static int CONFIG_LVLMAXMONTURE = 100;
	
	public static boolean USE_SUBSCRIBE = false;

	public static boolean CONFIG_USE_MOBS = false;
	public static boolean CONFIG_USE_IP = false;
	public static GameServer gameServer;
	public static RealmServer realmServer;
	public static boolean isRunning = false;
	public static boolean isSaving = false;
	public static boolean AURA_SYSTEM = false;

	public static ArrayList<Integer> arenaMap = new ArrayList<Integer>(8);
	public static int CONFIG_ARENA_TIMER = 10 * 60 * 1000;// 10 minutes
	public static int CONFIG_DB_COMMIT = 30 * 1000;
	public static int CONFIG_MAX_IDLE_TIME = 1800000;// En millisecondes
	public static ArrayList<Integer> NOTINHDV = new ArrayList<Integer>();

	public static boolean SHOW_RECV = false;
	public static int PLAYER_IP = 3;

	public static int serverID = 1;

	public static int pa = 12;
	public static int pm = 6;

	public static int CONFIG_TIME_REBOOT = 10800000;
	public static boolean CONFIG_REBOOT_AUTO = false;

	public static boolean CONFIG_ACTIVER_STATS_2 = false;

	public static short CONFIG_START_MAP = 7411;
	public static int CONFIG_START_CELL = 311;
	public static short CONFIG_MAP_PVP = 7411;
	public static int CONFIG_CELL_PVP = 311;
	public static short CONFIG_MAP_PVM = 7411;
	public static int CONFIG_CELL_PVM = 311;
	public static short CONFIG_MAP_SHOP = 7411;
	public static int CONFIG_CELL_SHOP = 311;

	public static boolean CONFIG_XP_DEFI = false;
	public static boolean ALLOW_MULE_XPDEFI = false;

	public static int MORPHID_SKIN = 300;

	// Baskwo
	public static int MORPH_VIP = 0;
	public static short MAP_VIP = 0;
	public static int CELL_VIP = 0;
	public static short MAP_EVENT = 0;
	public static int CELL_EVENT = 0;
	public static short MAP_POUNTCH = 0;
	public static int CELL_POUNTCH = 0;
	public static short MAP_SQUATTE = 0;
	public static int CELL_SQUATTE = 0;

	public static short CONFIG_MAP_ENCLOS = 8750;
	public static int CONFIG_CELL_ENCLOS = 468;

	public static long CONFIG_MS_PER_TURN = 30000;
	public static long CONFIG_MS_FOR_START_FIGHT = 25000;

	public static boolean CMD_FULLMORPH = false;
	
	public static void main(String[] args) {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				Ancestra.closeServers();
			}
		});
		Consolex.clear();
		PrintStream ps;
		try {
			ps = new PrintStream(System.out, true, "IBM850");
			System.setOut(ps);
		} catch (Exception e) {
			System.out
					.println("Erreur de conversion du format des caracteres de la console.");
		}
		Consolex.setTitle("CyonEmu - Version 2.1 - Chargement...");
		System.out
				.println("==============================================================\n");
		System.out.println(makeHeader());
		System.out
				.println("==============================================================\n");
		System.out.println("Chargement de la configuration..");
		loadConfiguration();
		isInit = true;
		System.out.println("Configuration OK.");
		System.out.println("Connexion au MySQL server.");
		if (SQLManager.setUpConnexion())
			System.out.println("Connexion OK.");
		else {
			System.out.println("Connexion invalide.");
			Ancestra.closeServers();
			System.exit(0);
		}
		System.out.println("Creation du Monde.");
		long startTime = System.currentTimeMillis();
		World.createWorld();
		long endTime = System.currentTimeMillis();
		long differenceTime = (endTime - startTime) / 1000;
		System.out.println("Emulator OK en : " + differenceTime + " s");
		isRunning = true;
		System.out
				.println("==============================================================\n");
		System.out.println("Lancement du serveur, PORT DU JEU: "
				+ CONFIG_GAME_PORT);
		String Ip = "";
		try {
			Ip = InetAddress.getLocalHost().getHostAddress();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e1) {
			}
			System.exit(1);
		}
		Ip = IP;
		gameServer = new GameServer(Ip);
		System.out.println("Lancement du serveur, PORT DU CONNEXION: "
				+ CONFIG_REALM_PORT);
		realmServer = new RealmServer();
		System.out.println("IP du serveur: " + IP);
		refreshTitle();
		EmuStart();
	}

	private static void loadConfiguration() {
		try {
			
			@SuppressWarnings("resource")
			BufferedReader config = new BufferedReader(new FileReader(
					CONFIG_FILE));
			String line = "";
			while ((line = config.readLine()) != null) {
				if (line.split("=").length == 1)
					continue;
				String param = line.split("=")[0].trim();
				String value = line.split("=")[1].trim();
				if (param.equalsIgnoreCase("DEBUG")) {
					if (value.equalsIgnoreCase("true")) {
						Ancestra.CONFIG_DEBUG = true;
						System.out.println("Modo Debug: ON");
					}
				} else if (param.equalsIgnoreCase("SEND_POLICY")) {
					if (value.equalsIgnoreCase("true")) {
						Ancestra.CONFIG_POLICY = true;
					}
				} else if (param.equalsIgnoreCase("START_ITEMS")) {
					if (value == "")
						START_ITEMS = null;
					else
						START_ITEMS = value;
				} else if (param.equalsIgnoreCase("SKIN_MORPHID")) {
					Ancestra.MORPHID_SKIN = Integer.parseInt(value);
				} else if (param.equalsIgnoreCase("START_KAMAs")) {
					Ancestra.CONFIG_START_KAMAS = Integer.parseInt(value);
					if (Ancestra.CONFIG_START_KAMAS < 0)
						Ancestra.CONFIG_START_KAMAS = 0;
					if (Ancestra.CONFIG_START_KAMAS > 1000000000)
						Ancestra.CONFIG_START_KAMAS = 1000000000;
				} else if (param.equalsIgnoreCase("LOAD_ACTION_DELAY")) {
					Ancestra.CONFIG_LOAD_DELAY = (Integer.parseInt(value) * 1000);
				} else if (param.equalsIgnoreCase("START_LEVEL")) {
					Ancestra.CONFIG_START_LEVEL = Integer.parseInt(value);
					if (Ancestra.CONFIG_START_LEVEL < 1)
						Ancestra.CONFIG_START_LEVEL = 1;
					if (Ancestra.CONFIG_START_LEVEL > 200)
						Ancestra.CONFIG_START_LEVEL = 200;
				} else if (param.equalsIgnoreCase("START_MAP")) {
					Ancestra.CONFIG_START_MAP = Short.parseShort(value);
				} else if (param.equalsIgnoreCase("START_CELL")) {
					Ancestra.CONFIG_START_CELL = Integer.parseInt(value);
				} else if (param.equalsIgnoreCase("ENCLOS_MAP")) {
					Ancestra.CONFIG_MAP_ENCLOS = Short.parseShort(value);
				} else if (param.equalsIgnoreCase("ENCLOS_CELL")) {
					Ancestra.CONFIG_CELL_ENCLOS = Integer.parseInt(value);
				} else if (param.equalsIgnoreCase("SHOP_MAP")) {
					Ancestra.CONFIG_MAP_SHOP = Short.parseShort(value);
				} else if (param.equalsIgnoreCase("SHOP_CELL")) {
					Ancestra.CONFIG_CELL_SHOP = Integer.parseInt(value);
				} else if (param.equalsIgnoreCase("PVM_MAP")) {
					Ancestra.CONFIG_MAP_PVM = Short.parseShort(value);
				} else if (param.equalsIgnoreCase("PVM_CELL")) {
					Ancestra.CONFIG_CELL_PVM = Integer.parseInt(value);
				} else if (param.equalsIgnoreCase("PVP_MAP")) {
					Ancestra.CONFIG_MAP_PVP = Short.parseShort(value);
				} else if (param.equalsIgnoreCase("PVP_CELL")) {
					Ancestra.CONFIG_CELL_PVP = Integer.parseInt(value);
				} else if (param.equalsIgnoreCase("KAMAS")) {
					Ancestra.KAMAS = Integer.parseInt(value);
				} else if (param.equalsIgnoreCase("HONOR")) {
					Ancestra.HONOR = Integer.parseInt(value);
				} else if (param.equalsIgnoreCase("SAVE_TIME")) {
					Ancestra.CONFIG_SAVE_TIME = Integer.parseInt(value) * 60 * 1000000000;
				} else if (param.equalsIgnoreCase("XP_PVM")) {
					Ancestra.XP_PVM = Integer.parseInt(value);
				} else if (param.equalsIgnoreCase("XP_PVP")) {
					Ancestra.XP_PVP = Integer.parseInt(value);
				} else if (param.equalsIgnoreCase("MAX_LEVEL_MONTURE")) {
					Ancestra.CONFIG_LVLMAXMONTURE = (Integer.parseInt(value));

				} else if (param.equalsIgnoreCase("ALLOW_MULE_XPDEFI")) {
					Ancestra.ALLOW_MULE_XPDEFI = value.equalsIgnoreCase("true");
				} else if (param.equalsIgnoreCase("ACTIVER_XP_DEFI")) {
					if (value.equalsIgnoreCase("true")) {
						Ancestra.CONFIG_XP_DEFI = true;
					}
				} else if (param.equalsIgnoreCase("LVL_PVP")) {
					Ancestra.LVL_PVP = Integer.parseInt(value);
				} else if (param.equalsIgnoreCase("PLAYER_IP")) {
					Ancestra.PLAYER_IP = Integer.parseInt(value);
				} else if (param.equalsIgnoreCase("DROP")) {
					Ancestra.CONFIG_DROP = Integer.parseInt(value);
				} else if (param.equalsIgnoreCase("MOTD")) {
					Ancestra.CONFIG_MOTD = line.split("=", 2)[1];
				} else if (param.equalsIgnoreCase("PORC_FM")) {
					Ancestra.PORC_FM = Integer.parseInt(value);
				} else if (param.equalsIgnoreCase("LOCALIP_LOOPBACK")) {
					if (value.equalsIgnoreCase("true")) {
						Ancestra.CONFIG_IP_LOOPBACK = true;
					}
				} else if (param.equalsIgnoreCase("ZAAP")) {
					if (value.equalsIgnoreCase("true")) {
						Ancestra.CONFIG_ZAAP = true;
					}
				} else if (param.equalsIgnoreCase("ACTIV_CARACT_2")) {
					if (value.equalsIgnoreCase("true")) {
						Ancestra.CONFIG_ACTIVER_STATS_2 = true;
					}
				} else if (param.equalsIgnoreCase("ACTIV_REBOOT")) {
					if (value.equalsIgnoreCase("true")) {
						Ancestra.CONFIG_REBOOT_AUTO = true;
					}
				} else if (param.equalsIgnoreCase("USE_IP")) {
					if (value.equalsIgnoreCase("true")) {
						Ancestra.CONFIG_USE_IP = true;
					}
				} else if (param.equalsIgnoreCase("MOTD_COLOR")) {
					Ancestra.CONFIG_MOTD_COLOR = value;
				} else if (param.equalsIgnoreCase("XP_METIER")) {
					Ancestra.XP_METIER = Integer.parseInt(value);
				} else if (param.equalsIgnoreCase("GAME_PORT")) {
					Ancestra.CONFIG_GAME_PORT = Integer.parseInt(value);
				} else if (param.equalsIgnoreCase("REALM_PORT")) {
					Ancestra.CONFIG_REALM_PORT = Integer.parseInt(value);
				} else if(param.equalsIgnoreCase("USE_SUBSCRIBE")) {
					Ancestra.USE_SUBSCRIBE = (value.equalsIgnoreCase("true") ? true : false); 
				} else if (param.equalsIgnoreCase("HOST_IP")) {
					Ancestra.IP = value;
				} else if (param.equalsIgnoreCase("DB_HOST")) {
					Ancestra.DB_HOST = value;
				} else if (param.equalsIgnoreCase("DB_USER")) {
					Ancestra.DB_USER = value;
				} else if (param.equalsIgnoreCase("DB_PASS")) {
					if (value == null)
						value = "";
					Ancestra.DB_PASS = value;
				} else if (param.equalsIgnoreCase("STATIC_DB_NAME")) {
					Ancestra.STATIC_DB_NAME = value;
				} else if (param.equalsIgnoreCase("OTHER_DB_NAME")) {
					Ancestra.OTHER_DB_NAME = value;
				} else if (param.equalsIgnoreCase("USE_MOBS")) {
					Ancestra.CONFIG_USE_MOBS = value.equalsIgnoreCase("true");
				} else if (param.equalsIgnoreCase("ALLOW_MULTI_ACCOUNT")) {
					Ancestra.CONFIG_ALLOW_MULTI = value
							.equalsIgnoreCase("true");
				} else if (param.equalsIgnoreCase("PLAYER_LIMIT")) {
					Ancestra.CONFIG_PLAYER_LIMIT = Integer.parseInt(value);
				} else if (param.equalsIgnoreCase("ARENA_MAP")) {
					for (String curID : value.split(",")) {
						Ancestra.arenaMap.add(Integer.parseInt(curID));
					}
				} else if (param.equalsIgnoreCase("ARENA_TIMER")) {
					Ancestra.CONFIG_ARENA_TIMER = Integer.parseInt(value);
				} else if (param.equalsIgnoreCase("AURA_SYSTEM")) {
					Ancestra.AURA_SYSTEM = value.equalsIgnoreCase("true");
				} else if (param.equalsIgnoreCase("ALLOW_MULE_PVP")) {
					Ancestra.ALLOW_MULE_PVP = value.equalsIgnoreCase("true");
				} else if (param.equalsIgnoreCase("MAX_IDLE_TIME")) {
					Ancestra.CONFIG_MAX_IDLE_TIME = (Integer.parseInt(value) * 60000);
				} else if (param.equalsIgnoreCase("SERVER_ID")) {
					serverID = Integer.parseInt(value);
				} else if (param.equalsIgnoreCase("NOT_IN_HDV")) {
					for (String curID : value.split(",")) {
						Ancestra.NOTINHDV.add(Integer.parseInt(curID));
					}
				} else if (param.equalsIgnoreCase("MAXPA")) {
					Ancestra.pa = Integer.parseInt(value);
				} else if (param.equalsIgnoreCase("MAXPM")) {
					Ancestra.pm = Integer.parseInt(value);
				} else if (param.equalsIgnoreCase("REBOOT_TIME")) {
					Ancestra.CONFIG_TIME_REBOOT = Integer.parseInt(value);
				} else if (param.equalsIgnoreCase("SHOW_RECV"))

				{
					Ancestra.SHOW_RECV = value.equalsIgnoreCase("true");
				}
				// Baskwo
				else if (param.equalsIgnoreCase("MORPH_VIP")) {
					Ancestra.MORPH_VIP = Integer.parseInt(value);
				} else if (param.equalsIgnoreCase("MAP_VIP")) {
					Ancestra.MAP_VIP = Short.parseShort(value);
				} else if (param.equalsIgnoreCase("CELL_VIP")) {
					Ancestra.CELL_VIP = Integer.parseInt(value);
				} else if (param.equalsIgnoreCase("MAP_EVENT")) {
					Ancestra.MAP_EVENT = Short.parseShort(value);
				} else if (param.equalsIgnoreCase("CELL_EVENT")) {
					Ancestra.CELL_EVENT = Integer.parseInt(value);
				} else if (param.equalsIgnoreCase("MAP_POUNTCH")) {
					Ancestra.MAP_POUNTCH = Short.parseShort(value);
				} else if (param.equalsIgnoreCase("CELL_POUNTCH")) {
					Ancestra.CELL_POUNTCH = Integer.parseInt(value);
				} else if (param.equalsIgnoreCase("MAP_SQUATTE")) {
					Ancestra.MAP_SQUATTE = Short.parseShort(value);
				} else if (param.equalsIgnoreCase("CELL_SQUATTE")) {
					Ancestra.CELL_SQUATTE = Integer.parseInt(value);
				}
				
				//Nicky31
				else if (param.equalsIgnoreCase("FULLMORPH")) {
					if (value.equalsIgnoreCase("true")) {
						Ancestra.CMD_FULLMORPH = true;
					}
				}
			}

			if (STATIC_DB_NAME == null || OTHER_DB_NAME == null
					|| DB_HOST == null || DB_PASS == null || DB_USER == null) {
				throw new Exception();
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out
					.println("Fichier de configuration non existant ou illisible !");
			System.out.println("Fermeture du serveur...");
			System.exit(1);
		}
	}

	public static void closeServers() {
		System.out.println("Fermeture du serveur...");
		if (isRunning) {
			isRunning = false;
			Ancestra.gameServer.kickAll();
			World.saveAll(null);
			SQLManager.closeCons();
		}
		System.out.println("Serveur ferm�.");
		isRunning = false;
	}

	public static String makeHeader() {
		StringBuilder mess = new StringBuilder();
		mess.append("-");
		mess.append("\nCyoneEmu Remake v" + Constants.SERVER_VERSION);
		mess.append("\nBy " + Constants.SERVER_MAKER + ".");
		mess.append("\nMerci a Diabu et DeathDown pour sa base.");
		mess.append("\n-");
		return mess.toString();
	}

	public static void EmuStart() {
		Consolex.clear();
		System.out
				.println("==============================================================\n");
		System.out.println(makeHeader());
		System.out
				.println("==============================================================\n");
		System.out.print("Chargement du serveur...");
		for (int i = 0; i < 40; i++) {
			System.out.print(".");
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
		System.out.println(". Ok.");
		Consolex.println("\nCyoneEmu OK ! En attente de connexion....",
				ConsoleColorEnum.BOLD);
		Consolex.println(
				"Help ou ? pour voir la liste du commande disponible dans cette console.",
				ConsoleColorEnum.RED);
		new Consolex();
	}

	public static void ReStart() {
		Consolex.clear();
		System.out
				.println("==============================================================\n");
		System.out.println(makeHeader());
		System.out
				.println("==============================================================\n");
		Consolex.println(
				"Help ou ? pour voir la liste du commande disponible dans cette console.",
				ConsoleColorEnum.YELLOW);
		new Consolex();
	}

	public static void refreshTitle() {
		if (!isRunning)
			return;
		StringBuilder title = new StringBuilder();
		title.append("CyoneEmu - REALMPort: ").append(CONFIG_REALM_PORT)
				.append(" GAMEPort: ").append(CONFIG_GAME_PORT);
		title.append(" En ligne: ").append(gameServer.getPlayerNumber())
				.append(" Statut: ");
		switch (World.get_state()) {
		case (short) 1:
			title.append("Disponible");
			break;
		case (short) 2:
			title.append("Save");
			break;
		default:
			title.append("Indisponnible");
			break;
		}
		Consolex.setTitle(title.toString());
	}
}
