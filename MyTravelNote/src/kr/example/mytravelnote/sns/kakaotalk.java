package kr.example.mytravelnote.sns;

import android.content.Context;

import com.kakao.KakaoLink;
import com.kakao.KakaoParameterException;
import com.kakao.KakaoTalkLinkMessageBuilder;

/* 카카오톡으로 전송 관련 구문 */
public class kakaotalk 
{
	/* KAKAO TALK 관련 변수 */
	private KakaoLink kakaoLink;
	private KakaoTalkLinkMessageBuilder kakaoTalkLinkMessageBuilder;
	
	/* Context */
	private Context mContext = null;
	
	/* TODO 기본 정보 관련 변수 */
	private String image_K = null; /* 이미지 전송 관련 구문 */
	private String overview_K = null; /* 상세 설명 전송 관련 구문 */
	private String name_K = null; /* 이름 전송 관련 구문 */
	private String address_K = null; /* 주소 전송 관련 구문 */
	private String useinfo_K = null; /* 이용 정보 전송 관련 구문 */
	
	/* TODO 관광지 정보 관련 변수 */
	private String pat_baby_K = null; /* 애견과 유모차 */
	private String parking_K = null; /* 주차정보 */
	private String restdate_K = null; /* 휴무일 - 행사정보 */
	
	/* TODO 관광정보 관련 생성자 */
	public kakaotalk(Context mContext, String name_K, String image_K, String address_K, String overview_K, String restdate_K, String parking_K, String useinfo_K, String pat_baby_K)
	{
		this.mContext = mContext;
		this.name_K = name_K; /* 관광지 명 */
		this.image_K = image_K; /* 관광지 사진 */
		this.address_K = address_K; /* 관광지 주소 */
		this.overview_K = overview_K; /* 관광지 설명 */
		this.restdate_K = restdate_K; /* 관광지 휴무일 */
		this.parking_K = parking_K; /* 관광지 주차정보 */
		this.useinfo_K = useinfo_K; /* 관광지 이용정보 */
		this.pat_baby_K = pat_baby_K; /* 관광지 애견과 유모차 정보 */
	}
	
	/* TODO 행사정보 관련 생성자 */
	public kakaotalk(Context mContext, String name_K, String image_K, String address_K, String overview_K, String useinfo_K, String restdate_K)
	{ this(mContext, name_K, image_K, address_K, overview_K, restdate_K, null, useinfo_K, null); }

	/* TODO KaKao Talk 메세지 작성 함수 */
	public void Festival_Kakao_Talk_write(int number) 
	{
		/* StringBuffer */
		StringBuffer str_Resutl_K = null; 
		
		switch(number)
		{
			case (1) : /* 관광 정보 */
			{ 
				str_Resutl_K = new StringBuffer(); /* StringBuffer 객체 생성 */
				str_Resutl_K.append(name_K); /* 제목 */
				str_Resutl_K.append("\n"); str_Resutl_K.append("주소 : "); str_Resutl_K.append(address_K); /* 주소 */
				str_Resutl_K.append("\n"); str_Resutl_K.append("문의 및 안내 : "); str_Resutl_K.append(overview_K); /* 문의 및 안내 */
				str_Resutl_K.append("\n"); str_Resutl_K.append("휴무일 : "); str_Resutl_K.append(restdate_K); /* 휴무일 */
				str_Resutl_K.append("\n"); str_Resutl_K.append("주차장소 : "); str_Resutl_K.append(parking_K); /* 주차장소 */
				str_Resutl_K.append("\n"); str_Resutl_K.append("이용시간 : "); str_Resutl_K.append(useinfo_K); /* 이용시간 */
				str_Resutl_K.append("\n"); str_Resutl_K.append("유모차대여 및 애완동물 : "); str_Resutl_K.append(pat_baby_K); /* 유모차 대여 및 애완동물  */
				break; 
			} 
			case (2) : /* 행사 정보 */
			{ 
				str_Resutl_K = new StringBuffer(); /* StringBuffer 객체 생성 */
				str_Resutl_K.append(name_K); /* 제목 */
				str_Resutl_K.append("\n"); str_Resutl_K.append("주소 : "); str_Resutl_K.append(address_K); /* 주소 */
				str_Resutl_K.append("\n"); str_Resutl_K.append("행사 내용 : "); str_Resutl_K.append(overview_K); /* 문의 및 안내 */
				str_Resutl_K.append("\n"); str_Resutl_K.append("주최사 정보 : "); str_Resutl_K.append(address_K); /* 주차장소 */
				str_Resutl_K.append("\n"); str_Resutl_K.append("이용 정보 : "); str_Resutl_K.append(useinfo_K); /* 이용정보 */
				break; 
			} 
		}			
			
			try 
			{
				/* KaKao Talk 관련 변수 구문 */	
				kakaoLink = KakaoLink.getKakaoLink(mContext); /* KaKao Talk 객체 생성 */
				kakaoTalkLinkMessageBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();
				kakaoTalkLinkMessageBuilder.addText(str_Resutl_K.toString()); /* Kakao talk 타이틀 입력 */
        		kakaoTalkLinkMessageBuilder.addImage(image_K, 500, 700); /* 카카오톡으로 이미지를 설정 하여서 보내는 구문 */
				kakaoLink.sendMessage(kakaoTalkLinkMessageBuilder.build(), mContext); /* 마지막으로 저장 된 메세지와 이미지를 보냄 */
			} catch (KakaoParameterException e) /* TODO Auto-generated catch block */ { e.printStackTrace(); }
	}
}