package ie.webware.catseyes;

public final class Constants
{
	private Constants() {}
		public static final String worldOmeterURL = "https://covid.ourworldindata.org/data/owid-covid-data.json";
		public static final String jsonWood = "[^a-zA-Z0-9._\\[\\]\\{\\}]";
		public static final String jsonTree = "[^a-zA-Z0-9._]";
		public static final String dbName = ":memory:";
		public static final String tblRegion = "Region";
		public static final String tblCountry = "Country";
		public static final String tblData = "Data";
		public static final String colCountry = "Country";
		public static final String tblPrimaryKey = "ID";
		public static final String tblFKRegion = "FK_REGION";
		public static final String tblFKCountry = "FK_COUNTRY";
		public static final String tblFKData = "FK_DATA";
}

