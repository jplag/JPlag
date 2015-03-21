package myCal;
public class Kalender {
	public int anzahlTage(Monat m, int jahr) {

		if (jahr == 0) {
			throw new IllegalArgumentException(
					"Es gibt kein Jahr null in der traditionellen christlichen Zeitrechnung");
		}

		if (m.equals(Monat.Februar) && istSschaltjahr(jahr)) {
			return 29;
		} else if (m.equals(Monat.Februar)) {
			return 28;
		}

		if (m.equals(Monat.Januar) || m.equals(Monat.Maerz)
				|| m.equals(Monat.Mai) || m.equals(Monat.Juli)
				|| m.equals(Monat.August) || m.equals(Monat.Oktober)
				|| m.equals(Monat.Dezember)) {
			return 31;
		}

		return 30;
	}

	private boolean istSschaltjahr(int jahr) {
		if ((jahr % 4 == 0 && jahr % 100 != 0) || jahr % 400 == 0) {
			return true;
		}
		return false;
	}
}
