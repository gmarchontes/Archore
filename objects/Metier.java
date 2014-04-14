package objects;

import game.GameThread;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.swing.Timer;

import common.Ancestra;

import objects.Personnage;

import common.Constants;
import common.Formulas;
import common.SQLManager;
import common.SocketManager;
import common.World;

public class Metier {
	private int _id;
	private ArrayList<Integer> _tools = new ArrayList<Integer>();
	private Map<Integer, ArrayList<Integer>> _crafts = new TreeMap<Integer, ArrayList<Integer>>();

	public Metier(int id, String tools, String crafts) {
		_id = id;
		if (!tools.equals("")) {
			for (String str : tools.split(",")) {
				try {
					int tool = Integer.parseInt(str);
					_tools.add(Integer.valueOf(tool));
				} catch (Exception localException) {
				}
			}
		}
		if (!crafts.equals("")) {
			for (String str : crafts.split("\\|")) {
				try {
					int skID = Integer.parseInt(str.split(";")[0]);
					ArrayList<Integer> list = new ArrayList<Integer>();
					for (String str2 : str.split(";")[1].split(","))
						list.add(Integer.valueOf(Integer.parseInt(str2)));
					_crafts.put(Integer.valueOf(skID), list);
				} catch (Exception localException1) {
				}
			}
		}
	}

	public ArrayList<Integer> getListBySkill(int skID) {
		return (ArrayList<Integer>) _crafts.get(Integer.valueOf(skID));
	}

	public boolean canCraft(int skill, int template) {
		if (_crafts.get(Integer.valueOf(skill)) != null)
			for (Iterator<?> localIterator = ((ArrayList<?>) _crafts
					.get(Integer.valueOf(skill))).iterator(); localIterator
					.hasNext();) {
				int a = ((Integer) localIterator.next()).intValue();
				if (a == template)
					return true;
			}
		return false;
	}

	public int getId() {
		return _id;
	}

	public boolean isValidTool(int t) {
		for (Iterator<Integer> localIterator = _tools.iterator(); localIterator
				.hasNext();) {
			int a = ((Integer) localIterator.next()).intValue();
			if (t == a)
				return true;
		}
		return false;
	}

	public static byte ViewActualStatsItem(Objet obj, String stats) {
		if (!obj.parseStatsString().isEmpty()) {
			for (Entry<Integer, Integer> entry : obj.getStats().getMap()
					.entrySet()) {
				if (Integer.toHexString(((Integer) entry.getKey()).intValue())
						.compareTo(stats) > 0) {
					if ((Integer.toHexString(
							((Integer) entry.getKey()).intValue()).compareTo(
							"98") == 0)
							&& (stats.compareTo("7b") == 0)) {
						return 2;
					}
					if ((Integer.toHexString(
							((Integer) entry.getKey()).intValue()).compareTo(
							"9a") == 0)
							&& (stats.compareTo("77") == 0)) {
						return 2;
					}
					if ((Integer.toHexString(
							((Integer) entry.getKey()).intValue()).compareTo(
							"9b") == 0)
							&& (stats.compareTo("7e") == 0)) {
						return 2;
					}
					if ((Integer.toHexString(
							((Integer) entry.getKey()).intValue()).compareTo(
							"9d") == 0)
							&& (stats.compareTo("76") == 0)) {
						return 2;
					}
					if ((Integer.toHexString(
							((Integer) entry.getKey()).intValue()).compareTo(
							"74") == 0)
							&& (stats.compareTo("75") == 0)) {
						return 2;
					}
					if ((Integer.toHexString(
							((Integer) entry.getKey()).intValue()).compareTo(
							"99") == 0)
							&& (stats.compareTo("7d") == 0)) {
						return 2;
					}

				} else if (Integer.toHexString(
						((Integer) entry.getKey()).intValue()).compareTo(stats) == 0) {
					return 1;
				}
			}
			return 0;
		}

		return 0;
	}

	public static byte ViewBaseStatsItem(Objet obj, String ItemStats) {
		String[] splitted = obj.getTemplate().getStrTemplate().split(",");
		for (String s : splitted) {
			String[] stats = s.split("#");
			if (stats[0].compareTo(ItemStats) > 0) {
				if ((stats[0].compareTo("98") == 0)
						&& (ItemStats.compareTo("7b") == 0)) {
					return 2;
				}
				if ((stats[0].compareTo("9a") == 0)
						&& (ItemStats.compareTo("77") == 0)) {
					return 2;
				}
				if ((stats[0].compareTo("9b") == 0)
						&& (ItemStats.compareTo("7e") == 0)) {
					return 2;
				}
				if ((stats[0].compareTo("9d") == 0)
						&& (ItemStats.compareTo("76") == 0)) {
					return 2;
				}
				if ((stats[0].compareTo("74") == 0)
						&& (ItemStats.compareTo("75") == 0)) {
					return 2;
				}
				if ((stats[0].compareTo("99") == 0)
						&& (ItemStats.compareTo("7d") == 0)) {
					return 2;
				}

			} else if (stats[0].compareTo(ItemStats) == 0) {
				return 1;
			}
		}
		return 0;
	}

	public static int getBaseMaxJet(int templateID, String statsModif) {
		Objet.ObjTemplate t = World.getObjTemplate(templateID);
		String[] splitted = t.getStrTemplate().split(",");
		for (String s : splitted) {
			String[] stats = s.split("#");
			if (stats[0].compareTo(statsModif) > 0) {
				continue;
			}
			if (stats[0].compareTo(statsModif) != 0)
				continue;
			int max = Integer.parseInt(stats[2], 16);
			if (max == 0)
				max = Integer.parseInt(stats[1], 16);
			return max;
		}

		return 0;
	}

	public static int getActualJet(Objet obj, String statsModif) {
		for (Entry<Integer, Integer> entry : obj.getStats().getMap().entrySet()) {
			if (Integer.toHexString(((Integer) entry.getKey()).intValue())
					.compareTo(statsModif) > 0) {
				continue;
			}
			if (Integer.toHexString(((Integer) entry.getKey()).intValue())
					.compareTo(statsModif) != 0)
				continue;
			int JetActual = ((Integer) entry.getValue()).intValue();
			return JetActual;
		}

		return 0;
	}

	public static class JobAction {
		private int _skID;
		private int _min = 1;
		private int _max = 1;
		private boolean _isCraft;
		private int _chan = 100;
		private int _time = 0;
		private int _xpWin = 0;
		private long _startTime;
		private Map<Integer, Integer> _ingredients = new TreeMap<Integer, Integer>();
		private Map<Integer, Integer> _lastCraft = new TreeMap<Integer, Integer>();
		private Timer _craftTimer;
		private Personnage _P;

		public JobAction(int sk, int min, int max, boolean craft, int arg,
				int xpWin) {
			_skID = sk;
			_min = min;
			_max = max;
			_isCraft = craft;
			if (craft)
				_chan = arg;
			else
				_time = arg;
			_xpWin = xpWin;

			_craftTimer = new Timer(100, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					craft();
					_craftTimer.stop();
				}
			});
		}

		public void endAction(Personnage P, Carte.InteractiveObject IO,
				GameThread.GameAction GA, Carte.Case cell) {
			if (!_isCraft) {
				if (_startTime - System.currentTimeMillis() > 500L)
					return;
				IO.setState(3);
				IO.startTimer();

				SocketManager.GAME_SEND_GDF_PACKET_TO_MAP(P.get_curCarte(),
						cell);

				boolean special = Formulas.getRandomValue(0, 99) == 0;

				int qua = _max > _min ? Formulas.getRandomValue(_min, _max)
						: _min;
				int tID = Constants.getObjectByJobSkill(_skID, special);

				Objet.ObjTemplate T = World.getObjTemplate(tID);
				if (T == null)
					return;
				Objet O = T.createNewItem(qua, false);

				if (P.addObjet(O, true))
					World.addObjet(O, true);
				SocketManager.GAME_SEND_IQ_PACKET(P, P.get_GUID(), qua);
				SocketManager.GAME_SEND_Ow_PACKET(P);
				int maxPercent = 20 + (P.getMetierBySkill(_skID).get_lvl() - 20);// 40(fixe)+(lvl
																					// metier
																					// -
																					// 20)
				if (P.getMetierBySkill(_skID).get_lvl() >= 20
						&& Formulas.getRandomValue(1, maxPercent) == maxPercent) {
					int[][] protectors = JobProtector.JOB_PROTECTORS;
					for (int i = 0; i < protectors.length; i++) {
						if (tID == protectors[i][1]) {
							int monsterId = protectors[i][0];
							int monsterLvl = JobProtector.getProtectorLvl(P
									.get_lvl());
							P.get_curCarte().startFigthVersusMonstres(
									P,
									new Monstre.MobGroup(
											P.get_curCarte()._nextObjectID,
											cell.getID(), monsterId + ","
													+ monsterLvl + ","
													+ monsterLvl));
							break;
						}
					}
				}
			}
		}

		public void startAction(Personnage P, Carte.InteractiveObject IO,
				GameThread.GameAction GA, Carte.Case cell) {
			_P = P;
			if (!_isCraft) {
				IO.setInteractive(false);
				IO.setState(2);
				SocketManager.GAME_SEND_GA_PACKET_TO_MAP(P.get_curCarte(), ""
						+ GA._id, 501, "" + P.get_GUID(), cell.getID() + ","
						+ _time);
				SocketManager.GAME_SEND_GDF_PACKET_TO_MAP(P.get_curCarte(),
						cell);
				_startTime = (System.currentTimeMillis() + _time);
			} else {
				P.set_away(true);
				IO.setState(2);
				P.setCurJobAction(this);
				SocketManager.GAME_SEND_ECK_PACKET(P, 3, _min + ";" + _skID);
				SocketManager.GAME_SEND_GDF_PACKET_TO_MAP(P.get_curCarte(),
						cell);
			}
		}

		public int getSkillID() {
			return _skID;
		}

		public int getMin() {
			return _min;
		}

		public int getXpWin() {
			return _xpWin;
		}

		public int getMax() {
			return _max;
		}

		public int getChance() {
			return _chan;
		}

		public int getTime() {
			return _time;
		}

		public boolean isCraft() {
			return _isCraft;
		}

		public void modifIngredient(Personnage P, int guid, int qua) {
			int q = _ingredients.get(Integer.valueOf(guid)) == null ? 0
					: ((Integer) _ingredients.get(Integer.valueOf(guid)))
							.intValue();

			_ingredients.remove(Integer.valueOf(guid));

			q += qua;
			if (q > 0) {
				_ingredients.put(Integer.valueOf(guid), Integer.valueOf(q));
				SocketManager.GAME_SEND_EXCHANGE_MOVE_OK(P, 'O', "+", guid
						+ "|" + q);
			} else {
				SocketManager
						.GAME_SEND_EXCHANGE_MOVE_OK(P, 'O', "-", "" + guid);
			}
		}

		public void craft() {
			if (!_isCraft)
				return;
			boolean signed = false;
			try {
				Thread.sleep(250L);
			} catch (Exception localException) {
			}
			if ((_skID == 1) || (_skID == 113) || (_skID == 115)
					|| (_skID == 116) || (_skID == 117) || (_skID == 118)
					|| (_skID == 119) || (_skID == 120)
					|| ((_skID >= 163) && (_skID <= 169))) {
				doFmCraft();
				return;
			}

			Map<Integer, Integer> items = new TreeMap<Integer, Integer>();

			for (Entry<Integer, Integer> e : _ingredients.entrySet()) {
				if (!_P.hasItemGuid(((Integer) e.getKey()).intValue())) {
					SocketManager.GAME_SEND_Ec_PACKET(_P, "EI");
					return;
				}

				Objet obj = World.getObjet(((Integer) e.getKey()).intValue());
				if (obj == null) {
					SocketManager.GAME_SEND_Ec_PACKET(_P, "EI");
					return;
				}

				if (obj.getQuantity() < ((Integer) e.getValue()).intValue()) {
					SocketManager.GAME_SEND_Ec_PACKET(_P, "EI");
					return;
				}

				int newQua = obj.getQuantity()
						- ((Integer) e.getValue()).intValue();

				if (newQua < 0)
					return;
				if (newQua == 0) {
					_P.removeItem(((Integer) e.getKey()).intValue());
					World.removeItem(((Integer) e.getKey()).intValue());
					SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(_P,
							((Integer) e.getKey()).intValue());
				} else {
					obj.setQuantity(newQua);
					SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(_P, obj);
				}

				items.put(Integer.valueOf(obj.getTemplate().getID()),
						(Integer) e.getValue());
			}

			if (items.containsKey(Integer.valueOf(7508)))
				signed = true;
			items.remove(Integer.valueOf(7508));

			SocketManager.GAME_SEND_Ow_PACKET(_P);

			Metier.StatsMetier SM = _P.getMetierBySkill(_skID);
			int tID = World.getObjectByIngredientForJob(SM.getTemplate()
					.getListBySkill(_skID), items);

			if ((tID == -1) || (!SM.getTemplate().canCraft(_skID, tID))) {
				SocketManager.GAME_SEND_Ec_PACKET(_P, "EI");
				SocketManager.GAME_SEND_IO_PACKET_TO_MAP(_P.get_curCarte(),
						_P.get_GUID(), "-");
				_ingredients.clear();

				return;
			}

			int chan = Constants.getChanceByNbrCaseByLvl(SM.get_lvl(),
					_ingredients.size());
			int jet = Formulas.getRandomValue(1, 100);// TODO
			boolean success = chan >= jet;

			if (!success) {
				SocketManager.GAME_SEND_Ec_PACKET(_P, "EF");
				SocketManager.GAME_SEND_IO_PACKET_TO_MAP(_P.get_curCarte(),
						_P.get_GUID(), "-" + tID);
				SocketManager.GAME_SEND_Im_PACKET(_P, "0118");
			} else {
				Objet newObj = World.getObjTemplate(tID)
						.createNewItem(1, false);

				if (signed)
					newObj.addTxtStat(988, _P.get_name());
				boolean add = true;
				int guid = newObj.getGuid();

				for (Entry<Integer, Objet> entry : _P.getItems().entrySet()) {
					Objet obj = (Objet) entry.getValue();
					if ((obj.getTemplate().getID() != newObj.getTemplate()
							.getID())
							|| (!obj.getStats().isSameStats(newObj.getStats()))
							|| (obj.getPosition() != -1))
						continue;
					obj.setQuantity(obj.getQuantity() + newObj.getQuantity());
					SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(_P, obj);
					add = false;
					guid = obj.getGuid();
				}

				if (add) {
					_P.getItems()
							.put(Integer.valueOf(newObj.getGuid()), newObj);
					SocketManager.GAME_SEND_OAKO_PACKET(_P, newObj);
					World.addObjet(newObj, true);
				}

				SocketManager.GAME_SEND_Ow_PACKET(_P);
				SocketManager.GAME_SEND_Em_PACKET(_P,
						"KO+" + guid + "|1|" + tID + "|"
								+ newObj.parseStatsString().replace(";", "#"));
				SocketManager.GAME_SEND_Ec_PACKET(_P, "K;" + tID);
				SocketManager.GAME_SEND_IO_PACKET_TO_MAP(_P.get_curCarte(),
						_P.get_GUID(), "+" + tID);
			}

			int winXP = Constants.calculXpWinCraft(SM.get_lvl(),
					_ingredients.size())
					* Ancestra.XP_METIER;
			if (success) {
				SM.addXp(_P, winXP);
				ArrayList<StatsMetier> SMs = new ArrayList<StatsMetier>();
				SMs.add(SM);
				SocketManager.GAME_SEND_JX_PACKET(_P, SMs);
			}

			_lastCraft.clear();
			_lastCraft.putAll(_ingredients);
			_ingredients.clear();
		}

		private void doFmCraft() {
			boolean signed = false;
			Objet obj = null;
			Objet sign = null;
			Objet mod = null;
			int isElementChanging = 0, stat = -1, isStatsChanging = 0, add = 0;
			int poid = 0;
			String stats = "-1";
			int runeID = -1;
			for (Iterator<Integer> localIterator = _ingredients.keySet()
					.iterator(); localIterator.hasNext();) {
				int guid = ((Integer) localIterator.next()).intValue();

				Objet ing = World.getObjet(guid);
				if ((!_P.hasItemGuid(guid)) || (ing == null)) {
					SocketManager.GAME_SEND_Ec_PACKET(_P, "EI");
					SocketManager.GAME_SEND_IO_PACKET_TO_MAP(_P.get_curCarte(),
							_P.get_GUID(), "-");
					_ingredients.clear();
					return;
				}
				int id = ing.getTemplate().getID();
				runeID = id;
				switch (id) {
				case 1333:// Potion Etincelle
					stat = 99;
					isElementChanging = ing.getTemplate().getLevel();
					mod = ing;
					break;
				case 1335:// Potion crachin
					stat = 96;
					isElementChanging = ing.getTemplate().getLevel();
					mod = ing;
					break;
				case 1337:// Potion de courant d'air
					stat = 98;
					isElementChanging = ing.getTemplate().getLevel();
					mod = ing;
					break;
				case 1338:// Potion de secousse
					stat = 97;
					isElementChanging = ing.getTemplate().getLevel();
					mod = ing;
					break;
				case 1340:// Potion d'eboulement
					stat = 97;
					isElementChanging = ing.getTemplate().getLevel();
					mod = ing;
					break;
				case 1341:// Potion Averse
					stat = 96;
					isElementChanging = ing.getTemplate().getLevel();
					mod = ing;
					break;
				case 1342:// Potion de rafale
					stat = 98;
					isElementChanging = ing.getTemplate().getLevel();
					mod = ing;
					break;
				case 1343:// Potion de Flamb�e
					stat = 99;
					isElementChanging = ing.getTemplate().getLevel();
					mod = ing;
					break;
				case 1345:// Potion Incendie
					stat = 99;
					isElementChanging = ing.getTemplate().getLevel();
					mod = ing;
					break;
				case 1346:// Potion Tsunami
					stat = 96;
					isElementChanging = ing.getTemplate().getLevel();
					mod = ing;
					break;
				case 1347:// Potion Ouragan
					stat = 98;
					isElementChanging = ing.getTemplate().getLevel();
					mod = ing;
					break;
				case 1348:// Potion de seisme
					stat = 97;
					isElementChanging = ing.getTemplate().getLevel();
					mod = ing;
					break;

				case 1519:// Force
					mod = ing;
					stats = "76";
					add = 1;
					poid = 1;
					isStatsChanging = ing.getTemplate().getLevel();
					break;
				case 1521:// Sagesse
					mod = ing;
					stats = "7c";
					add = 1;
					poid = 3;
					isStatsChanging = ing.getTemplate().getLevel();
					break;
				case 1522:// Intel
					mod = ing;
					stats = "7e";
					add = 1;
					poid = 1;
					isStatsChanging = ing.getTemplate().getLevel();
					break;
				case 1523:// Vita
					mod = ing;
					stats = "7d";
					add = 3;
					poid = 1;
					isStatsChanging = ing.getTemplate().getLevel();
					break;
				case 1524:// Agi
					mod = ing;
					stats = "77";
					add = 1;
					poid = 1;
					isStatsChanging = ing.getTemplate().getLevel();
					break;
				case 1525:// Chance
					mod = ing;
					stats = "7b";
					add = 1;
					poid = 1;
					isStatsChanging = ing.getTemplate().getLevel();
					break;
				case 1545:// Pa force
					mod = ing;
					stats = "76";
					add = 3;
					poid = 3;
					isStatsChanging = ing.getTemplate().getLevel();
					break;
				case 1546:// Pa Sagesse
					mod = ing;
					stats = "7c";
					add = 3;
					poid = 9;
					isStatsChanging = ing.getTemplate().getLevel();
					break;
				case 1547:// Pa Intel
					mod = ing;
					stats = "7e";
					add = 3;
					poid = 3;
					isStatsChanging = ing.getTemplate().getLevel();
					break;
				case 1548:// Pa VI
					mod = ing;
					stats = "7d";
					add = 10;
					poid = 10;
					isStatsChanging = ing.getTemplate().getLevel();
					break;
				case 1549:// Pa age
					mod = ing;
					stats = "77";
					add = 3;
					poid = 3;
					isStatsChanging = ing.getTemplate().getLevel();
					break;
				case 1550:// Pa cha
					mod = ing;
					stats = "7b";
					add = 3;
					poid = 10;
					isStatsChanging = ing.getTemplate().getLevel();
					break;
				case 1551:// Ra Fo
					mod = ing;
					stats = "76";
					add = 10;
					poid = 10;
					isStatsChanging = ing.getTemplate().getLevel();
					break;
				case 1552:// Ra Sa
					mod = ing;
					stats = "7c";
					add = 10;
					poid = 30;
					isStatsChanging = ing.getTemplate().getLevel();
					break;
				case 1553:// Ra Ine
					mod = ing;
					stats = "7e";
					add = 10;
					poid = 10;
					isStatsChanging = ing.getTemplate().getLevel();
					break;
				case 1554:// Ra Vi
					mod = ing;
					stats = "7d";
					add = 30;
					poid = 10;
					isStatsChanging = ing.getTemplate().getLevel();
					break;
				case 1555:// Ra Age
					mod = ing;
					stats = "77";
					add = 10;
					poid = 10;
					isStatsChanging = ing.getTemplate().getLevel();
					break;
				case 1556:// Ra cha
					mod = ing;
					stats = "7b";
					add = 10;
					poid = 10;
					isStatsChanging = ing.getTemplate().getLevel();
					break;
				case 1557:// Ga PA
					mod = ing;
					stats = "6f";
					add = 1;
					poid = 100;
					isStatsChanging = ing.getTemplate().getLevel();
					break;
				case 1558:// Ga PME
					mod = ing;
					stats = "80";
					add = 1;
					poid = 90;
					isStatsChanging = ing.getTemplate().getLevel();
					break;
				case 7433:// Cri
					mod = ing;
					stats = "73";
					add = 1;
					poid = 30;
					isStatsChanging = ing.getTemplate().getLevel();
					break;
				case 7434:// Soins
					mod = ing;
					stats = "b2";
					add = 1;
					poid = 20;
					isStatsChanging = ing.getTemplate().getLevel();
					break;
				case 7435:// Dommages
					mod = ing;
					stats = "70";
					add = 1;
					poid = 20;
					isStatsChanging = ing.getTemplate().getLevel();
					break;
				case 7436:// Domages %
					mod = ing;
					stats = "8a";
					add = 1;
					poid = 2;
					isStatsChanging = ing.getTemplate().getLevel();
					break;
				case 7437:// Domage renvoy�
					mod = ing;
					stats = "dc";
					add = 1;
					poid = 2;
					isStatsChanging = ing.getTemplate().getLevel();
					break;
				case 7438:// Porter
					mod = ing;
					stats = "75";
					add = 1;
					poid = 50;
					isStatsChanging = ing.getTemplate().getLevel();
					break;
				case 7442:// Invoque
					mod = ing;
					stats = "b6";
					add = 1;
					poid = 30;
					isStatsChanging = ing.getTemplate().getLevel();
					break;
				case 7443:// Pod
					mod = ing;
					stats = "9e";
					add = 10;
					poid = 1; // ?
					isStatsChanging = ing.getTemplate().getLevel();
					break;
				case 7444:// Pa pod
					mod = ing;
					stats = "9e";
					add = 30;
					poid = 1; // ?
					isStatsChanging = ing.getTemplate().getLevel();
					break;
				case 7445:// Ra pod
					mod = ing;
					stats = "9e";
					add = 100;
					poid = 1; // ?
					isStatsChanging = ing.getTemplate().getLevel();
					break;
				case 7446:// Pi�ge
					mod = ing;
					stats = "e1";
					add = 1;
					poid = 15;
					isStatsChanging = ing.getTemplate().getLevel();
					break;
				case 7447:// Pi�ge %
					mod = ing;
					stats = "e2";
					add = 1;
					poid = 2;
					isStatsChanging = ing.getTemplate().getLevel();
					break;
				case 7448:// Initiative
					mod = ing;
					stats = "ae";
					add = 10;
					poid = 1;
					isStatsChanging = ing.getTemplate().getLevel();
					break;
				case 7449:// Pa Initiative
					mod = ing;
					stats = "ae";
					add = 30;
					poid = 3;
					isStatsChanging = ing.getTemplate().getLevel();
					break;
				case 7450:// Ra Initiative
					mod = ing;
					stats = "ae";
					add = 100;
					poid = 10;
					isStatsChanging = ing.getTemplate().getLevel();
					break;
				case 7451:// Prospec
					mod = ing;
					stats = "b0";
					add = 1;
					poid = 3;
					isStatsChanging = ing.getTemplate().getLevel();
					break;
				case 7452:// R� Feu
					mod = ing;
					stats = "f3";
					add = 1;
					poid = 4;
					isStatsChanging = ing.getTemplate().getLevel();
					break;
				case 7453:// R� Air
					mod = ing;
					stats = "f2";
					add = 1;
					poid = 4;
					isStatsChanging = ing.getTemplate().getLevel();
					break;
				case 7454:// R� Eau
					mod = ing;
					stats = "f1";
					add = 1;
					poid = 4;
					isStatsChanging = ing.getTemplate().getLevel();
					break;
				case 7455:// R� Terre
					mod = ing;
					stats = "f0";
					add = 1;
					poid = 4;
					isStatsChanging = ing.getTemplate().getLevel();
					break;
				case 7456:// R� Neutre
					mod = ing;
					stats = "f4";
					add = 1;
					poid = 4;
					isStatsChanging = ing.getTemplate().getLevel();
					break;
				case 7457:// R� % Feu
					mod = ing;
					stats = "d5";
					add = 1;
					poid = 5;
					isStatsChanging = ing.getTemplate().getLevel();
					break;
				case 7458:// R� % Air
					mod = ing;
					stats = "d4";
					add = 1;
					poid = 5;
					isStatsChanging = ing.getTemplate().getLevel();
					break;
				case 7459:// R� % Terre
					mod = ing;
					stats = "d2";
					add = 1;
					poid = 5;
					isStatsChanging = ing.getTemplate().getLevel();
					break;
				case 7460:// R� % neutre
					mod = ing;
					stats = "d6";
					add = 1;
					poid = 5;
					isStatsChanging = ing.getTemplate().getLevel();
					break;
				case 7560:// R� % Eau
					mod = ing;
					stats = "d3";
					add = 1;
					poid = 5;
					isStatsChanging = ing.getTemplate().getLevel();
					break;
				case 8379:
					mod = ing;

					isStatsChanging = ing.getTemplate().getLevel();
					break;
				case 7508:
					signed = true;
					sign = ing;
					break;
				default:
					if (ing.getTemplate().getPACost() > 0)
						obj = ing;
					if ((ing.getTemplate().getType() != 1)
							&& (ing.getTemplate().getType() != 2)
							&& (ing.getTemplate().getType() != 3)
							&& (ing.getTemplate().getType() != 4)
							&& (ing.getTemplate().getType() != 5)
							&& (ing.getTemplate().getType() != 6)
							&& (ing.getTemplate().getType() != 7)
							&& (ing.getTemplate().getType() != 8)
							&& (ing.getTemplate().getType() != 9)
							&& (ing.getTemplate().getType() != 10)
							&& (ing.getTemplate().getType() != 11)
							&& (ing.getTemplate().getType() != 16)
							&& (ing.getTemplate().getType() != 17)
							&& (ing.getTemplate().getType() != 19)
							&& (ing.getTemplate().getType() != 20)
							&& (ing.getTemplate().getType() != 21)
							&& (ing.getTemplate().getType() != 22)
							&& (ing.getTemplate().getType() != 81)
							&& (ing.getTemplate().getType() != 102)
							&& (ing.getTemplate().getType() != 114))
						continue;
					obj = ing;
				}
			}

			Metier.StatsMetier SM = _P.getMetierBySkill(_skID);

			if ((SM == null) || (obj == null) || (mod == null)) {
				SocketManager.GAME_SEND_Ec_PACKET(_P, "EI");
				SocketManager.GAME_SEND_IO_PACKET_TO_MAP(_P.get_curCarte(),
						_P.get_GUID(), "-");
				_ingredients.clear();
				return;
			}

			if (SM.get_lvl() * 2 < obj.getTemplate().getLevel()) {
				isElementChanging = 0;
				isStatsChanging = 0;
			}

			int chan = 80;
			int nivelOficio = SM.get_lvl();
			double Coef = 1;

			if ((isStatsChanging > 0) && (isElementChanging == 0)) {
				int poidActual = 1;
				int ActualJet = 1;
				if (!obj.parseStatsString().isEmpty()) {
					poidActual = Objet.getPoidOfActualItem(obj
							.parseStatsString().replace(";", "#"));
					ActualJet = Metier.getActualJet(obj, stats);
				}
				int poidBase = Objet.getPoidOfBaseItem(obj.getTemplate()
						.getID());
				int BaseMaxJet = Metier.getBaseMaxJet(
						obj.getTemplate().getID(), stats);

				if (poidBase <= 0) {
					poidBase = 0;
				}
				if (BaseMaxJet <= 0) {
					BaseMaxJet = 0;
				}
				if (ActualJet <= 0) {
					ActualJet = 0;
				}
				if (poidActual <= 0) {
					poidActual = 0;
				}
				if (poid <= 0) {
					poid = 0;
				}

				Coef = 1.0D;
				if (((Metier.ViewBaseStatsItem(obj, stats) == 1) && (Metier
						.ViewActualStatsItem(obj, stats) == 1))
						|| ((Metier.ViewBaseStatsItem(obj, stats) == 1) && (Metier
								.ViewActualStatsItem(obj, stats) == 0))) {
					Coef = 1.0D;
				} else if ((Metier.ViewBaseStatsItem(obj, stats) == 2)
						&& (Metier.ViewActualStatsItem(obj, stats) == 2)) {
					Coef = 0.75D;
				} else if (((Metier.ViewBaseStatsItem(obj, stats) == 0) && (Metier
						.ViewActualStatsItem(obj, stats) == 0))
						|| ((Metier.ViewBaseStatsItem(obj, stats) == 0) && (Metier
								.ViewActualStatsItem(obj, stats) == 1))) {
					Coef = 0.25D;
				}

				int JetMax = BaseMaxJet
						* (2 - (obj.getTemplate().getLevel() / 100));
				if (JetMax <= 0)
					JetMax = 1;

				Coef = Coef * ((JetMax - (double) (ActualJet)) / 25);
				if (Coef <= 0)
					Coef = 0;
				chan = Formulas.ChanceFM(poidBase, poidActual, ActualJet, poid,
						JetMax, Coef);
				if (runeID == 1557 || runeID == 1558) {
					chan = 20 - (int) (Math.sqrt(130 - nivelOficio) * 2D);
				} else
					chan = 20 - (int) (Math.sqrt(80 - nivelOficio) * 2D);

				if (chan <= 0)
					chan = 1;
				if (chan >= 80)
					chan = 80;

				SocketManager.GAME_SEND_MESSAGE(_P,
						"Votre chance de succ�s �tait de " + chan + "%",
						"009900");
				SocketManager.GAME_SEND_MESSAGE(_P,
						"Vous pouvez toujours ajouter "
								+ (BaseMaxJet - (ActualJet + add))
								+ " stats avec une certitude de r�ussir.",
						"009900");

			}

			int jet = Formulas.getRandomValue(1, 100);
			boolean success = chan >= jet;
			int tID = obj.getTemplate().getID();

			if (runeID == 1557) {
				int chancesPA = Formulas.getRandomValue(1, 6);
				if (chancesPA != 4)
					success = false;
			}

			if ((success) && (Formulas.getRandomValue(1, 5) == 3))
				success = false;

			if (!success) {
				String statsnegatif = "";
				if (Metier.ViewBaseStatsItem(obj, "98") == 1) {
					if ((Metier.ViewActualStatsItem(obj, "98") == 0)
							&& (Metier.ViewActualStatsItem(obj, "7b") == 0)) {
						statsnegatif = statsnegatif + ",98#"
								+ Integer.toHexString(1) + "#0#0#0d0+1";
					}
				}
				if (Metier.ViewBaseStatsItem(obj, "9a") == 1) {
					if ((Metier.ViewActualStatsItem(obj, "9a") == 0)
							&& (Metier.ViewActualStatsItem(obj, "77") == 0)) {
						statsnegatif = statsnegatif + ",9a#"
								+ Integer.toHexString(1) + "#0#0#0d0+1";
					}
				}
				if (Metier.ViewBaseStatsItem(obj, "9b") == 1) {
					if ((Metier.ViewActualStatsItem(obj, "9b") == 0)
							&& (Metier.ViewActualStatsItem(obj, "7e") == 0)) {
						statsnegatif = statsnegatif + ",9b#"
								+ Integer.toHexString(1) + "#0#0#0d0+1";
					}
				}
				if (Metier.ViewBaseStatsItem(obj, "9d") == 1) {
					if ((Metier.ViewActualStatsItem(obj, "9d") == 0)
							&& (Metier.ViewActualStatsItem(obj, "76") == 0)) {
						statsnegatif = statsnegatif + ",9d#"
								+ Integer.toHexString(1) + "#0#0#0d0+1";
					}
				}
				if (Metier.ViewBaseStatsItem(obj, "74") == 1) {
					if ((Metier.ViewActualStatsItem(obj, "74") == 0)
							&& (Metier.ViewActualStatsItem(obj, "75") == 0)) {
						statsnegatif = statsnegatif + ",74#"
								+ Integer.toHexString(1) + "#0#0#0d0+1";
					}
				}
				if (Metier.ViewBaseStatsItem(obj, "99") == 1) {
					if ((Metier.ViewActualStatsItem(obj, "99") == 0)
							&& (Metier.ViewActualStatsItem(obj, "7d") == 0)) {
						statsnegatif = statsnegatif + ",99#"
								+ Integer.toHexString(1) + "#0#0#0d0+1";
					}
				}
				if ((obj.parseStatsString().isEmpty())
						&& (!statsnegatif.isEmpty())) {
					obj.setStats(obj.generateNewStatsFromTemplate(
							statsnegatif.substring(1), false));
				} else if (!obj.parseStatsString().isEmpty()) {
					obj.setStats(obj.generateNewStatsFromTemplate(
							obj.parseFMEchecStatsString(obj, poid).replace(";",
									"#")
									+ statsnegatif, false));
				}
				SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(_P, obj.getGuid());
				SocketManager.GAME_SEND_Ow_PACKET(_P);
				SocketManager.GAME_SEND_OAKO_PACKET(_P, obj);
				SocketManager.GAME_SEND_Em_PACKET(_P,
						"EO+" + obj.getGuid() + "|1|" + tID + "|"
								+ obj.parseStatsString().replace(";", "#"));
				SocketManager.GAME_SEND_Ec_PACKET(_P, "EF");
				SocketManager.GAME_SEND_IO_PACKET_TO_MAP(_P.get_curCarte(),
						_P.get_GUID(), "-" + tID);
				SocketManager.GAME_SEND_Im_PACKET(_P, "0183");

				if (_P.getMetierBySkill(getSkillID()).get_lvl() <= 99)
					_P.getMetierBySkill(getSkillID()).addXp(_P, 50L);
			} else {
				int coef = 0;
				if (isElementChanging == 1)
					coef = 50;
				if (isElementChanging == 25)
					coef = 65;
				if (isElementChanging == 50)
					coef = 85;

				if (signed)
					obj.addTxtStat(985, _P.get_name());

				if ((isElementChanging > 0) && (isStatsChanging == 0)) {
					for (SpellEffect SE : obj.getEffects()) {
						if (SE.getEffectID() != 100)
							continue;

						String[] infos = SE.getArgs().split(";");
						try {
							int min = Integer.parseInt(infos[0], 16);
							int max = Integer.parseInt(infos[1], 16);
							int newMin = (int) ((min * coef) / 100);
							int newMax = (int) ((max * coef) / 100);

							if (newMin == 0)
								newMin = 1;
							String newJet = "1d" + (newMax - newMin + 1) + "+"
									+ (newMin - 1);
							String newArgs = Integer.toHexString(newMin) + ";"
									+ Integer.toHexString(newMax) + ";-1;-1;0;"
									+ newJet;

							SE.setArgs(newArgs);
							SE.setEffectID(stat);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} else if ((isStatsChanging > 0) && (isElementChanging == 0)) {
					boolean negatif = false;

					if (Metier.ViewActualStatsItem(obj, stats) == 2) {
						if (stats.compareTo("7b") == 0) {
							stats = "98";
							negatif = true;
						}
						if (stats.compareTo("77") == 0) {
							stats = "9a";
							negatif = true;
						}
						if (stats.compareTo("7e") == 0) {
							stats = "9b";
							negatif = true;
						}
						if (stats.compareTo("76") == 0) {
							stats = "9d";
							negatif = true;
						}
						if (stats.compareTo("75") == 0) {
							stats = "74";
							negatif = true;
						}
						if (stats.compareTo("7d") == 0) {
							stats = "99";
							negatif = true;
						}

					}

					if ((Metier.ViewActualStatsItem(obj, stats) == 1)
							|| (Metier.ViewActualStatsItem(obj, stats) == 2)) {
						obj.setStats(obj.generateNewStatsFromTemplate(obj
								.parseFMStatsString(stats, obj, add, negatif)
								.replace(";", "#"), false));
					} else {
						if (obj.parseStatsString().isEmpty()) {
							obj.setStats(obj.generateNewStatsFromTemplate(stats
									+ "#" + Integer.toHexString(add)
									+ "#0#0#0d0+" + add, false));
						} else {
							obj.setStats(obj.generateNewStatsFromTemplate(
									obj.parseFMStatsString(stats, obj, add,
											negatif).replace(";", "#")
											+ ","
											+ stats
											+ "#"
											+ Integer.toHexString(add)
											+ "#0#0#0d0+" + add, false));
						}
					}

				}

				SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(_P, obj.getGuid());
				SocketManager.GAME_SEND_Ow_PACKET(_P);
				SocketManager.GAME_SEND_OAKO_PACKET(_P, obj);
				SocketManager.GAME_SEND_Em_PACKET(_P,
						"KO+" + obj.getGuid() + "|1|" + tID + "|"
								+ obj.parseStatsString().replace(";", "#"));
				SocketManager.GAME_SEND_Ec_PACKET(_P, "K;" + tID);
				SocketManager.GAME_SEND_IO_PACKET_TO_MAP(_P.get_curCarte(),
						_P.get_GUID(), "+" + tID);
				SQLManager.SAVE_ITEM(obj);

				if (_P.getMetierBySkill(getSkillID()).get_lvl() <= 99) {
					_P.getMetierBySkill(getSkillID()).addXp(_P, 150L);
				}

			}

			if (sign != null) {
				int newQua = sign.getQuantity() - 1;

				if (newQua <= 0) {
					_P.removeItem(sign.getGuid());
					World.removeItem(sign.getGuid());
					SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(_P,
							sign.getGuid());
				} else {
					sign.setQuantity(newQua);
					SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(_P, sign);
				}
			}

			if (mod != null) {
				int newQua = mod.getQuantity() - 1;

				if (newQua <= 0) {
					_P.removeItem(mod.getGuid());
					World.removeItem(mod.getGuid());
					SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(_P,
							mod.getGuid());
				} else {
					mod.setQuantity(newQua);
					SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(_P, mod);
				}

			}

			_lastCraft.clear();
			_lastCraft.putAll(_ingredients);
			_ingredients.clear();
		}

		public void repeat(int time, Personnage P) {
			_craftTimer.stop();

			_lastCraft.clear();
			_lastCraft.putAll(_ingredients);
			for (int a = time; a >= 0; a--) {
				SocketManager.GAME_SEND_EA_PACKET(P, "" + a);
				_ingredients.clear();
				_ingredients.putAll(_lastCraft);
				craft();
			}
			SocketManager.GAME_SEND_Ea_PACKET(P, "1");
		}

		public void startCraft(Personnage P) {
			_craftTimer.start();
		}

		public void putLastCraftIngredients() {
			if (_P == null)
				return;
			if (_lastCraft == null)
				return;
			if (!_ingredients.isEmpty())
				return;
			_ingredients.clear();
			_ingredients.putAll(_lastCraft);
			for (Entry<Integer, Integer> e : _ingredients.entrySet()) {
				if (World.getObjet(((Integer) e.getKey()).intValue()) == null)
					return;
				if (World.getObjet(((Integer) e.getKey()).intValue())
						.getQuantity() < ((Integer) e.getValue()).intValue())
					return;
				SocketManager.GAME_SEND_EXCHANGE_MOVE_OK(_P, 'O', "+",
						e.getKey() + "|" + e.getValue());
			}
		}

		public void resetCraft() {
			_ingredients.clear();
			_lastCraft.clear();
		}
	}

	public static class StatsMetier {
		private int _id;
		private Metier _template;
		private int _lvl;
		private long _xp;
		private ArrayList<Metier.JobAction> _posActions = new ArrayList<JobAction>();
		private boolean _isCheap = false;
		private boolean _freeOnFails = false;
		private boolean _noRessource = false;
		private Metier.JobAction _curAction;
		private int _slotsPublico;
		private int _posicion;

		public StatsMetier(int id, Metier tp, int lvl, long xp) {
			_id = id;
			_template = tp;
			_lvl = lvl;
			_xp = xp;
			_posActions = Constants.getPosActionsToJob(tp.getId(), lvl);
		}

		public int get_lvl() {
			return _lvl;
		}

		public boolean isCheap() {
			return _isCheap;
		}

		public void setIsCheap(boolean isCheap) {
			_isCheap = isCheap;
		}

		public boolean isFreeOnFails() {
			return _freeOnFails;
		}

		public void setFreeOnFails(boolean freeOnFails) {
			_freeOnFails = freeOnFails;
		}

		public boolean isNoRessource() {
			return _noRessource;
		}

		public void setNoRessource(boolean noRessource) {
			_noRessource = noRessource;
		}

		public void setSlotsPublico(int slots) {
			_slotsPublico = slots;
		}

		public int getSlotsPublico() {
			return _slotsPublico;
		}

		public int getPosicion() {
			return _posicion;
		}

		public void levelUp(Personnage P, boolean send) {
			_lvl += 1;
			_posActions = Constants.getPosActionsToJob(_template.getId(), _lvl);

			if (send) {
				ArrayList<StatsMetier> list = new ArrayList<StatsMetier>();
				list.add(this);
				SocketManager.GAME_SEND_JS_PACKET(P, list);
				SocketManager.GAME_SEND_STATS_PACKET(P);
				SocketManager.GAME_SEND_Ow_PACKET(P);
				SocketManager.GAME_SEND_JN_PACKET(P, _template.getId(), _lvl);
				SocketManager.GAME_SEND_JO_PACKET(P, list);
			}
		}

		public String parseJS() {
			StringBuilder str = new StringBuilder();
			str.append("|").append(_template.getId()).append(";");
			boolean first = true;
			for (JobAction JA : _posActions) {
				if (!first)
					str.append(",");
				else
					first = false;
				str.append(JA.getSkillID()).append("~").append(JA.getMin())
						.append("~");
				if (JA.isCraft())
					str.append("0~0~").append(JA.getChance());
				else
					str.append(JA.getMax()).append("~0~").append(JA.getTime());
			}
			return str.toString();
		}

		public long getXp() {
			return _xp;
		}

		public void startAction(int id, Personnage P,
				Carte.InteractiveObject IO, GameThread.GameAction GA,
				Carte.Case cell) {
			for (Metier.JobAction JA : _posActions) {
				if (JA.getSkillID() != id)
					continue;
				_curAction = JA;
				JA.startAction(P, IO, GA, cell);
				return;
			}
		}

		public void endAction(int id, Personnage P, Carte.InteractiveObject IO,
				GameThread.GameAction GA, Carte.Case cell) {
			if (_curAction == null)
				return;
			_curAction.endAction(P, IO, GA, cell);
			addXp(P, _curAction.getXpWin() * Ancestra.XP_METIER);

			ArrayList<StatsMetier> list = new ArrayList<StatsMetier>();
			list.add(this);
			SocketManager.GAME_SEND_JX_PACKET(P, list);
		}

		public void addXp(Personnage P, long xp) {
			if (_lvl > 99)
				return;
			int exLvl = _lvl;
			_xp += xp;

			while ((_xp >= World.getExpLevel(_lvl + 1).metier) && (_lvl < 100)) {
				levelUp(P, false);
			}

			if ((_lvl > exLvl) && (P.isOnline())) {
				ArrayList<StatsMetier> list = new ArrayList<StatsMetier>();
				list.add(this);

				SocketManager.GAME_SEND_JS_PACKET(P, list);
				SocketManager.GAME_SEND_JN_PACKET(P, _template.getId(), _lvl);
				SocketManager.GAME_SEND_STATS_PACKET(P);
				SocketManager.GAME_SEND_Ow_PACKET(P);
				SocketManager.GAME_SEND_JO_PACKET(P, list);
			}
		}

		public String getXpString(String s) {
			String str = World.getExpLevel(_lvl).metier + s;
			str = str + _xp + s;
			str = str + World.getExpLevel(_lvl < 100 ? _lvl + 1 : _lvl).metier;
			return str;
		}

		public Metier getTemplate() {
			return _template;
		}

		public int getOptBinValue() {
			int nbr = 0;
			nbr += (_isCheap ? 1 : 0);
			nbr += (_freeOnFails ? 2 : 0);
			nbr += (_noRessource ? 4 : 0);
			return nbr;
		}

		public boolean isValidMapAction(int id) {
			for (Metier.JobAction JA : _posActions)
				if (JA.getSkillID() == id)
					return true;
			return false;
		}

		public void setOptBinValue(int bin) {
			_isCheap = false;
			_freeOnFails = false;
			_noRessource = false;

			if (bin - 4 >= 0) {
				bin -= 4;
				_isCheap = true;
			}
			if (bin - 2 >= 0) {
				bin -= 2;
				_freeOnFails = true;
			}
			if (bin - 1 >= 0) {
				bin--;
				_noRessource = true;
			}
		}

		public int getID() {
			return _id;
		}
	}
}