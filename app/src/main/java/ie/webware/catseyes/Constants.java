package ie.webware.catseyes;

public final class Constants
 {
  private Constants() {}
  public static final String worldOmeterURL = "https://covid.ourworldindata.org/data/owid-covid-data.json";
  public static final String jsonPath = "/worldometer.json";
  public static final String dbName = "worldometer.db";
  public static final String pkId = "ID";
  public static final String fkRegion = "FK_REGION";
  public static final String fkCountry = "FK_COUNTRY";
  public static final String tblJSON = "JSON";
  public static final String tblRegion = "REGION";
  public static final String tblCountry = "COUNTRY";
  public static final String tblData = "DATA";
  public static final String colJSON = "JSON";
  public static final String colCountryCode = "COUNTRY_CODE";
  public static final String colContinent = "CONTINENT";
  public static final String UITerra = "UITerra";
  public static final String UIContinent = "UIContinent";
  public static final String UIRegion = "UIRegion";
  public static final String UICountry = "UICountry";
  public static final String UICountryData = "UICountryData";
  public static final String UITerraData = "UITerraData";
  public static final String UITerraPopulation = "Population";
  public static final String rNought = "R0";
  public static final int oneMillion = 1000000;
 }

