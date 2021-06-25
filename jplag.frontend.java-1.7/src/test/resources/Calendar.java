package myCal;
public class Calendar {
	public int numberOfDays(Month m, int year) {

		if (year == 0) {
			throw new IllegalArgumentException(
					"There is no year zero in the traditional Christian calendar");
		}

		if (m.equals(Month.February) && isLeapYear(year)) {
			return 29;
		} else if (m.equals(Month.February)) {
			return 28;
		}

		if (m.equals(Month.January) || m.equals(Month.March)
				|| m.equals(Month.May) || m.equals(Month.July)
				|| m.equals(Month.August) || m.equals(Month.October)
				|| m.equals(Month.December)) {
			return 31;
		}

		return 30;
	}

	private boolean isLeapYear(int year) {
		if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
			return true;
		}
		return false;
	}
}
