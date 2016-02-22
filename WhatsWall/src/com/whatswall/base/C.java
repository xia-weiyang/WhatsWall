package com.whatswall.base;

public class C {
	
	public static int screenWidth = -1;
	public static int screenHeight = -1;
	
	public static final int BITMAP_SIZE = 200;
	public static final String BITMAP_FORMAT = ".JPG";

	public static final String CLASS_USER = "_User";
	public static final String USERNAME = "username";
	public static final String NICKNAME = "nickname";
	public static final String MOBILEPHONRNUMBER = "mobilePhoneNumber";
	public static final String SEX = "sex";
	public static final String SIGN = "sign";
	public static final String USERIMG = "userimg";
	public static final String PASSWORD = "password";
	public static final String ISSETPASSWORD = "isSetPassword";
	public static final String OBJECTID = "objectId";
	
	public static final String UPDATEDAT = "updatedAt";
	
	public static final String ClASS_ROOM = "Room";
	public static final String ROOM_NUMBER = "number";
	public static final String ROOM_ID = "roomId";
	public static final String ROOM_WELCOME = "welcome";
	public static final String ROOM_CREATEUSEROBJECTID="createUserObjectId";
	
	public static final String ClASS_CONTENT = "Content";
	public static final int CONTENT_TYPE_TIME = 0; 
	public static final int CONTENT_TYPE_ONLYTEXT = 1; 
	public static final int CONTENT_TYPE_ONLYIMG = 2; 
	public static final int CONTENT_TYPE_TEXTANDIMG = 3; 
	public static final String CONTENT_ROOMID = "roomId"; 
	public static final String CONTENT_CONTENTTYPE = "contentType"; 
	public static final String CONTENT_ISANON = "isAnon";
	public static final String CONTENT_CONTENT = "content";
	public static final String CONTENT_CONTENTID = "contentId";
	public static final String CONTENT_USER = "author"; 
	public static final String CONTENT_IMG = "img"; 
	public static final String CONTENT_IMGRELATION = "imgRelation"; 
	public static final String CONTENT_LIKE = "like";
	public static final String CONTENT_IMGWIDTHHEIGHT = "imgWidthHeight";
	
	public static final String CLASS_COMMENT = "Comment";
	public static final String COMMENT_USER = "author"; 
	public static final String COMMENT_CONTENT = "content";
	public static final String COMMENT_ROOMID = "roomId";
	public static final String COMMENT_COMMENTID = "commentId";
	public static final String COMMENT_CONTENTID = "contentId";
	
	public static final String CLASS_FAVORITE = "Favorite";
	public static final String FAVORITE_ROOMNUMBER = "favoriteNumbers";
	public static final String FAVROITE_USER = "user";
	public static final String FAVROITE_NOTE = "note";
	
	public static final String CLASS_LIKEROOM = "LikeRoom";
	public static final String LIKEROOM_USER = "user";
	public static final String LIKEROOM_ROOMID = "roomId";
	public static final String LIKEROOM_LIKE = "like";
	
	public static final String CLASS_REPORT = "Report";
	public static final String REPORT_USER = "user";
	public static final String REPORT_TYPE = "type";
	public static final String REPORT_NOTE = "note";
	public static final String REPORT_CONTENT = "content";
	
	public static final String CLASS_FEEDBACK = "Feedback";
	public static final String FEEDBACK_USER = "anthor";
	public static final String FEEDBACK_TYPE = "type";
	public static final String FEEDBACK_CONTENT = "content";
	
	public static final String CLASS_VERSION = "Version";
	public static final String VERSION_VERSION = "version";
	public static final String VERSION_VERSIONINFO = "versionInfo";
	public static final String VERSION_PLATFORM = "platform";
}
