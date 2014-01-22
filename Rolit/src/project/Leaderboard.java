package project;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Leaderboard implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Map<Integer, LinkedList<Object>> board = new HashMap<Integer, LinkedList<Object>>();

	private static final int NAME = 0;
	private static final int SCORE = 1;
	private static final int DATE = 2;
	private static final int TIME = 3;

	public Leaderboard() {
		// Auto-generated constructor stub
	}

	/**
	 * Voeg een score toe aan het leaderboard
	 * 
	 * @param name
	 *            de naam van het team/de speler
	 * @param score
	 *            de score van het team/de speler
	 */
	/*
	 * @ requires name != null; requires score != null; requires score
	 * instanceof Comparable; ensures \old(getBoard()).size() ==
	 * getBoard().size() + 1;
	 */
	public void add(String name, Object score) {
		assert name != null && score != null && score instanceof Comparable : "Er was een probleem met het scoretype";
		// Maak de set klaar
		LinkedList<Object> res = new LinkedList<Object>();
		res.add(name);
		res.add(score);
		res.add((getCurrentDate()));
		res.add((getCurrentTime()));

		// Gooi dit nu in het leaderboard op een nieuw ID.
		int ID = board.keySet().toArray().length;
		board.put(ID, res);
	}

	// Methoden die alle scores teruggeeft
	/**
	 * Geeft het hele leaderboard terug
	 * 
	 * @return het leaderboard
	 */
	/* @ pure */public Map<Integer, LinkedList<Object>> getBoard() {
		return copy(board);
	}

	/**
	 * Geeft een visuele weergave van een leaderboard dat is meegegeven
	 * 
	 * @param board
	 *            een board waarvan een weergave moet worden gegeven
	 */
	/*
	 * @ requires ditboard != null;
	 */
	public void showBoard(Map<Integer, LinkedList<Object>> ditboard) {
		System.out.println("----LEADERBOARD----");
		System.out.println("+-----" + ("ID") + "-----+----" + ("Name")
				+ "----+---" + ("Score") + "----+----Date----+---Time---+");
		for (int i = 0; i < ditboard.size(); i++) {
			Object[] ID = ditboard.keySet().toArray();
			LinkedList<Object> res = ditboard.get(ID[i]);
			Object[] res2 = res.toArray();
			System.out.println("| " + outline(ID[i]) + " | "
					+ outline(res2[NAME]) + " | " + outline(res2[SCORE])
					+ " | " + (res2[DATE]) + " | " + (res2[TIME]) + " |");
		}
	}

	/**
	 * Geeft een visuele weergave van een leaderboard dat is meegegeven
	 * 
	 * @param board
	 *            een board waarvan een weergave moet worden gegeven
	 */
	/*
	 * @ requires ditboard != null;
	 */
	public Object[] getShowBoard(Map<Integer, LinkedList<Object>> ditboard) {
		LinkedList<String> res = new LinkedList<String>();
		res.add("----LEADERBOARD----\n");
		res.add("+-----" + ("ID") + "-----+----" + ("Name") + "----+---"
				+ ("Score") + "----+----Date----+---Time---+\n");
		for (int i = 0; i < ditboard.size(); i++) {
			Object[] ID = ditboard.keySet().toArray();
			LinkedList<Object> res1 = ditboard.get(ID[i]);
			Object[] res2 = res1.toArray();
			res.add("/ " + outline(ID[i]) + " / " + outline(res2[NAME])
					+ " / " + outline(res2[SCORE]) + " / " + (res2[DATE])
					+ " / " + (res2[TIME]) + " /\n");
		}
		return res.toArray();
	}

	/**
	 * Geeft een visuele weergave van het totale leaderboard
	 */
	/*
	 * @
	 */
	public void showBoard() {
		showBoard(getBoard());
	}

	/**
	 * Geeft de highscore van de top n.
	 * 
	 * @param topn
	 *            het aantal teams dat moet worden weergegeven
	 * @return een map die je kunt visualiseren met showBoard
	 */
	/*
	 * @ requires topn <= getBoard().size();
	 */
	public Map<Integer, LinkedList<Object>> getHighscore(int topn) {
		// Om de highscore te vinden moet je de hoogste n scores vinden.
		// Om die te kunnen vinden moet je de lijst sorteren op score.
		Map<Integer, LinkedList<Object>> sortedBoard = (getBoard());//Hij moet hier worden gesorteerd
		Map<Integer, LinkedList<Object>> res = new HashMap<Integer, LinkedList<Object>>();
		Object[] ID = sortedBoard.keySet().toArray();
		if (topn < sortedBoard.size()) {
			for (int i = 0; i < topn; i++) {
				// Haal de i-de score op
				LinkedList<Object> values = sortedBoard.get(ID[i]);
				res.put((Integer) ID[i], values);
			}
		} else {
			for (int i = 0; i < sortedBoard.size(); i++) {
				// Haal de i-de score op
				LinkedList<Object> values = sortedBoard.get(ID[i]);
				res.put((Integer) ID[i], values);
			}
		}
		return res;

	}

	/**
	 * Sorteert het leaderboard op score
	 * 
	 * @param teSorteren
	 *            een ongesorteerd leaderboard
	 * @return een gesorteerd leaderboard op score
	 */
	public Map<Integer, LinkedList<Object>> sort(
			Map<Integer, LinkedList<Object>> teSorteren) {

		Map<Integer, LinkedList<Object>> res = new HashMap<Integer, LinkedList<Object>>();
		int lengte = teSorteren.size();
		for (int i = 0; i < lengte; i++) {
			Integer key = keyWithLowestScore(teSorteren); // Je wil de laagste
															// score omdat je
															// die telkens
															// bovenaan toevoegt
			LinkedList<Object> value = teSorteren.get(key);
			if (value != null) {
				res.put((lengte - i), value);
			}
			teSorteren.remove(key);
		}
		return res;
	}

	/**
	 * Geeft alle scores van iedereen terug die boven een bepaalde score heeft
	 * gehaald
	 * 
	 * @param score
	 *            de score die minimaal behaald moeten zijn
	 * @return een map die je kunt visualiseren met showBoard
	 */
	public Map<Integer, LinkedList<Object>> scoresAbove(Object score) {
		// Om de scores te vinden die boven een bepaalde score zitten moet je
		// vergelijken.
		// Om die te kunnen vinden moet je de lijst sorteren op score.
		Map<Integer, LinkedList<Object>> sortedBoard = sort(getBoard());
		Map<Integer, LinkedList<Object>> res = new HashMap<Integer, LinkedList<Object>>();
		Object[] ID = sortedBoard.keySet().toArray();
		int maxIndex = sortedBoard.size();
		for (int i = 0; i < sortedBoard.size(); i++) {
			// Kijk op welke i de score kleiner is dan weten we op welke index
			// het element zit dat niet meer mee hoeft met het resultaat
			Object[] values = sortedBoard.get(ID[i]).toArray();
			if (!isBiggerThan(values[SCORE], score)
					&& !values[SCORE].equals(score)) {
				// De score is niet meer groter of gelijk dan de minimale score
				maxIndex = i;
				break;
			}
		}
		// Haal alle elementen op tot aan de maxIndex
		for (int i = 0; i < maxIndex; i++) {
			res.put((Integer) sortedBoard.keySet().toArray()[i],
					sortedBoard.get(sortedBoard.keySet().toArray()[i]));
		}
		return res;

	}

	/**
	 * Kijkt of het eerste object groter is dan het tweede object
	 * 
	 * @param object
	 * @param score2
	 * @return true als het eerste object groter is
	 */
	/*
	 * @ requires object != null; requires score2 != null; requires object
	 * instanceof Comparable; requires score2 instanceof Comparable;
	 */
	@SuppressWarnings("unchecked")
	private boolean isBiggerThan(Object object, Object score2) {
		assert object != null && score2 != null && object instanceof Comparable
				&& score2 instanceof Comparable : "Er was een probleem met het scoretype";
		if (object != null && score2 == null) {
			return true;
		} else if (object == null && score2 != null) {
			return false;
		} else if (object == null && score2 == null) {
			return true;
		}
		return ((Comparable<Object>) object).compareTo(score2) > 0;
		// return (Double.compare(((Number) object).doubleValue(),((Number)
		// score2).doubleValue()) > 0);
	}

	/**
	 * Geeft een gemiddelde score van alle scores die in het leaderboard staan
	 * 
	 * @return een object met de gemiddelde score.
	 */
	public Object averageScore() {
		// Zet eerst ALLE scores in een array
		Object[] scores = new Object[board.size()];
		Object[] ID = board.keySet().toArray();

		for (int i = 0; i < scores.length; i++) {
			LinkedList<Object> res = board.get(ID[i]);
			Object[] res2 = res.toArray();
			scores[i] = res2[SCORE];
		}
		// Tel nu alle scores bij elkaar op
		int sum = 0;
		for (Object d : scores)
			sum = sum + (int) d;
		// Geef nu het gemiddelde
		if (scores.length > 0) {
			return sum / scores.length;
		} else {
			return 0;
		}

	}

	/**
	 * Geeft de gemiddelde score van de scores die behaald zijn op een bepaalde
	 * dag.
	 * 
	 * @param day
	 *            de dag waarnaar gekeken moet worden
	 * @return een object met de gemiddelde score
	 */
	public Object averageScoreOfDay(String day) {
		// Zet eerst alle scores die op de dag vallen in een lijst
		LinkedList<Object> linkedScores = new LinkedList<Object>();
		Object[] ID = board.keySet().toArray();
		for (int i = 0; i < board.size(); i++) {
			LinkedList<Object> res = board.get(ID[i]);
			Object[] res2 = res.toArray();
			if (getDate((String) res2[DATE]).equals(getDate(day))) {
				linkedScores.add(res2[SCORE]);
			} else {
				System.out.println("Deze dag(" + day
						+ ") zit niet in de lijst. Het was namelijk: "
						+ res2[DATE]);
			}
		}

		// Maak er nu een array van
		Object[] scores = linkedScores.toArray();

		// Tel nu alle scores bij elkaar op
		int sum = 0;
		for (Object d : scores)
			sum = sum + (int) d;

		// Geef nu het gemiddelde
		if (scores.length > 0) {
			return sum / scores.length;
		} else {
			return 0;
		}

	}

	// Methoden die resultaten van teams teruggeeft
	/**
	 * Geeft de beste score van een team
	 * 
	 * @param teamname
	 *            de naam van het team
	 * @return een object met de beste score
	 */
	public Object bestTeamscore(String teamname) {
		Map<Integer, LinkedList<Object>> sortedBoard = sort(getBoard());
		Object[] ID = sortedBoard.keySet().toArray();
		for (int i = 0; i < sortedBoard.size(); i++) {
			LinkedList<Object> res = board.get(ID[i]);
			Object[] res2 = res.toArray();
			if (res2[NAME].equals(teamname)) {
				return res2[SCORE];
			}
		}
		return 0;
	}

	/**
	 * Geeft de dag waarop de beste teamscore is neergezet
	 * 
	 * @param teamname
	 *            de naam van het team
	 * @return een datum
	 */
	public Date dayWithBestTeamscore(String teamname) {
		Map<Integer, LinkedList<Object>> sortedBoard = sort(getBoard());
		Object[] ID = sortedBoard.keySet().toArray();
		for (int i = 0; i < sortedBoard.size(); i++) {
			LinkedList<Object> res = board.get(ID[i]);
			Object[] res2 = res.toArray();
			if (res2[NAME].equals(teamname)) {
				return getDate((String) res2[DATE]);
			}
		}
		return getDate("00-00-0000");
	}

	// Hulpmethoden
	/**
	 * Geeft de huidige datum
	 * 
	 * @return
	 * @throws ParseException
	 */
	public String getCurrentDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		return sdf.format(date);
	}

	public String getCurrentTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		Date date = new Date();
		return sdf.format(date);
	}

	public Date getDate(String date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return sdf.parse(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public Date getTime(String time) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		try {
			return sdf.parse(time);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private Integer keyWithLowestScore(
			Map<Integer, LinkedList<Object>> teSorteren) {

		Object[] ID = teSorteren.keySet().toArray();
		int res = (int) ID[0];
		for (int i = 0 + 1; i < teSorteren.size(); i++) {
			LinkedList<Object> lista = teSorteren.get(ID[i]);
			// LinkedList<Object> listb = teSorteren.get(ID[res]);//
			LinkedList<Object> listb = teSorteren.get(res);
			if (isBiggerThan(listb.get(SCORE), lista.get(SCORE)))
				res = i;
		}
		return res;
	}

	private String outline(Object text) {
		return String.format("%1$-10s", text);
	}

	private Map<Integer, LinkedList<Object>> copy(
			Map<Integer, LinkedList<Object>> toCopy) {

		Map<Integer, LinkedList<Object>> copy = new HashMap<Integer, LinkedList<Object>>();
		Object[] keyArray = toCopy.keySet().toArray();

		for (int i = 0; i < toCopy.size(); i++) {
			Object key = keyArray[i];
			LinkedList<Object> value = toCopy.get(key);
			copy.put((Integer) key, value);
		}
		return copy;
	}

	public void add(String name, String score, String date, String time) {
		assert name != null && score != null && score instanceof Comparable : "Er was een probleem met het scoretype";
		// Maak de set klaar
		LinkedList<Object> res = new LinkedList<Object>();
		res.add(name);
		res.add(score);
		res.add(date);
		res.add(time);

		// Gooi dit nu in het leaderboard op een nieuw ID.
		int ID = board.keySet().toArray().length;
		board.put(ID, res);
	}
}
