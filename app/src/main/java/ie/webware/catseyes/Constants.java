package ie.webware.catseyes;

public final class Constants
 {
  private Constants() {}
  public static final String worldOmeterURL = "https://covid.ourworldindata.org/data/owid-covid-data.json";
  public static final String dbName = "worldometer.db";
  //public static final String dbName = null; // in memory
  public static final String dbPath = "/worldometer.json";
  public static final String tblRegion = "Region";
  public static final String CountryCode = "Country_Code";
  public static final String tblCountry = "Country";
  public static final String tblData = "Data";
  public static final String colContinent = "Continent";
  public static final String pkId = "ID";
  public static final String fkRegion = "FK_REGION";
  public static final String fkCountry = "FK_COUNTRY";
  public static final String isCountryLead = "continent";
  public static final String isDataLead = "date";
  public static final String[] lstCountry = new String[] {
   "AUS",
   "IRL",
   "ITA",
   "GBR"
  };
 }

