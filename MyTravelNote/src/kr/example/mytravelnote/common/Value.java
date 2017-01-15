package kr.example.mytravelnote.common;

public final class Value
{
	/* TODO 지역관련코드 저장 변수 */
	private static int AdminAreaCode = 0;
	private static int RegionAreaCode = 0;
	
	/* TODO 지역명관련코드 저장 변수 */
	private static String AdminArea = null;
	private static String LocalityArea = null;
	private static String ThoroughfareArea = null;
	private static String FeatureNameArea = null;
	
	/* TODO 지역관련코드 저장 메소드 */
	public final static void setAreaCode(int num1, int num2)
	{ AdminAreaCode = num1; RegionAreaCode = num2; }
	
	/* TODO 특별시/도/광역시 코드 호출 메소드 */
	public final static int getAdminAreaCode() { return AdminAreaCode; }
	/* TODO 시/구/군 코드 호출 메소드 */
	public final static int getRegionAreaCode() { return RegionAreaCode; }
	
	/* TODO 지역명관련코드 저장 메소드 */
	public final static void setArea(String str1, String str2, String str3, String str4)
	{ AdminArea = str1; LocalityArea = str2; ThoroughfareArea = str3; FeatureNameArea = str4; }
	
	/* TODO 특별시/도/광역시 명 호출 메소드 */
	public final static String getAdminArea() { return AdminArea; }
	/* TODO 시/구/군 명 호출 메소드  */
	public final static String getLocalityArea() { return LocalityArea; }
	/* TODO 동/읍/면 명 호출 메소드 */
	public final static String getThoroughFareArea() { return ThoroughfareArea; }
	/* TODO 우편번호 명 호출 메소드 */
	public final static String getFeatureNameArea() { return FeatureNameArea; }
}