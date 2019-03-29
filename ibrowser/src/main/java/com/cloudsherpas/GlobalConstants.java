package com.cloudsherpas;

import java.util.Arrays;
import java.util.List;

import com.cloudsherpas.model.IPRangeItem;

public interface GlobalConstants {
	
	static final String USER_AUTOCREATE_WHITELIST_REGEX = ".*@cloudsherpas.com|.*@johnlewis.co.uk|.*@dev.johnlewis.co.uk|.*@test.jog|.*@test.johnlewis.co.uk|.*@waitrose.co.uk";
	public static final String KEY_REGEX = "(([a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)*)*)";
	
	public static final String APP_ROOT = "/app";
	public static final String ADMIN_ROOT = "/admin";
	public static final String AUDIT_ROOT = "/audit";
	public static final String TASK_QUEUE = "/task";
	
	public static final String AJAX_LISTING = "AJAX_LISTING";
	public static final String ADMIN_GROUP = "IBROWSER ADMINISTRATORS";
	public static final String ADMIN_GROUP_NOUPPERCASE = "iBrowser Administrators";
//	public static final String ADMIN_GROUP = "IBROWSER ADMINS";
//	public static final String ADMIN_GROUP_NOUPPERCASE = "iBrowser Admins";
	public static final int PAGE_SIZE = 10;
	public static final String DEVICE_TYPE = "DEVICE_TYPE";
	
	//public static final String DEV_LOGIN = "https://johnlewislogin.appspot.com/loginpageservlet?DomainName=4jT7iswUCQJ%2BtWNzJ1iNtg%3D%3D&SAMLRequest=fVLJTsMwEL0j8Q%2BW79nKKqsJKiBEJZaIBg7cXGecmDp28TgN%2FD1uCgIOcPTz81vGMz176zTZgENlTU6zOKUEjLC1Mk1OH6ur6JSeFft7U%2BSdXrNZ71vzAK89oCfhpUE2XuS0d4ZZjgqZ4R0g84ItZrc3bBKnbO2st8JqSuaXOVWNllpYqZTUUqxELYWARtRGt2LZrpoWlvVSdZKSp69Yk22sOWIPc4OeGx%2BgNDuM0oMoPamyCTs4ZtnRMyXlp9O5MrsG%2F8Va7kjIrquqjMr7RTUKbFQN7i6wc9pY22iIhe229iVHVJsAS64RKJkhgvMh4IU12HfgFuA2SsDjw01OW%2B%2FXyJJkGIb4WybhyYttjYZBYTjH%2FSrhAmkxjpeNDd2Puf6fn3%2F50%2BLbYZr8kCo%2Bv23bZn5ZWq3EO5lpbYcLB9yHKt71ocmVdR33f7tlcTYiqo7kSGW9wTUIJRXUlCTFzvX3foSt%2BQA%3D&RelayState=https%3A%2F%2Faccounts.google.com%2FCheckCookie%3Fcontinue%3Dhttps%253A%252F%252Fappengine.google.com%252F_ah%252Fconflogin%253Fcontinue%253Dhttps%25253A%25252F%25252Fcs-ibrowser-dev.appspot.com%25252Fapp%26shdf%3DCiQLEgZhaG5hbWUaGGlCcm93c2VyIEpMIFByaW1hcnkgVGVzdAwSAmFoIhSRyogabh24Wb9qiBOWWYDcgHPKCygBMhSlemvtQoyh86E3rLMozUQ5vvnNiQ%26service%3Dah%26ltmpl%3Dgm";
	//public static final String TEST_LOGIN = "https://johnlewislogin.appspot.com/loginpageservlet?DomainName=4jT7iswUCQJ%2BtWNzJ1iNtg%3D%3D&SAMLRequest=fVLJTsMwEL0j8Q%2BW79nKKqsJKiBEJZaIBg7cXGecmDp28TgN%2FD1uCgIOcPTz81vGMz176zTZgENlTU6zOKUEjLC1Mk1OH6ur6JSeFft7U%2BSdXrNZ71vzAK89oCfhpUE2XuS0d4ZZjgqZ4R0g84ItZrc3bBKnbO2st8JqSuaXOVWNllpYqZTUUqxELYWARtRGt2LZrpoWlvVSdZKSp69Yk22sOWIPc4OeGx%2BgNDuM0oMoPamyCTs4ZtnRMyXlp9O5MrsG%2F8Va7kjIrquqjMr7RTUKbFQN7i6wc9pY22iIhe229iVHVJsAS64RKJkhgvMh4IU12HfgFuA2SsDjw01OW%2B%2FXyJJkGIb4WybhyYttjYZBYTjH%2FSrhAmkxjpeNDd2Puf6fn3%2F50%2BLbYZr8kCo%2Bv23bZn5ZWq3EO5lpbYcLB9yHKt71ocmVdR33f7tlcTYiqo7kSGW9wTUIJRXUlCTFzvX3foSt%2BQA%3D&RelayState=https%3A%2F%2Faccounts.google.com%2FCheckCookie%3Fcontinue%3Dhttps%253A%252F%252Fappengine.google.com%252F_ah%252Fconflogin%253Fcontinue%253Dhttps%25253A%25252F%25252Fimposing-fx-485.appspot.com%25252Fapp%26shdf%3DCiQLEgZhaG5hbWUaGGlCcm93c2VyIEpMIFByaW1hcnkgVGVzdAwSAmFoIhSRyogabh24Wb9qiBOWWYDcgHPKCygBMhSlemvtQoyh86E3rLMozUQ5vvnNiQ%26service%3Dah%26ltmpl%3Dgm";
	//public static final String PRIMARY_TEST_LOGIN = "https://johnlewislogin.appspot.com/loginpageservlet?DomainName=4jT7iswUCQJ%2BtWNzJ1iNtg%3D%3D&SAMLRequest=fVLJTsMwEL0j8Q%2BW79nKKqsJKiBEJZaIBg7cXGecmDp28TgN%2FD1uCgIOcPTz81vGMz176zTZgENlTU6zOKUEjLC1Mk1OH6ur6JSeFft7U%2BSdXrNZ71vzAK89oCfhpUE2XuS0d4ZZjgqZ4R0g84ItZrc3bBKnbO2st8JqSuaXOVWNllpYqZTUUqxELYWARtRGt2LZrpoWlvVSdZKSp69Yk22sOWIPc4OeGx%2BgNDuM0oMoPamyCTs4ZtnRMyXlp9O5MrsG%2F8Va7kjIrquqjMr7RTUKbFQN7i6wc9pY22iIhe229iVHVJsAS64RKJkhgvMh4IU12HfgFuA2SsDjw01OW%2B%2FXyJJkGIb4WybhyYttjYZBYTjH%2FSrhAmkxjpeNDd2Puf6fn3%2F50%2BLbYZr8kCo%2Bv23bZn5ZWq3EO5lpbYcLB9yHKt71ocmVdR33f7tlcTYiqo7kSGW9wTUIJRXUlCTFzvX3foSt%2BQA%3D&RelayState=https%3A%2F%2Faccounts.google.com%2FCheckCookie%3Fcontinue%3Dhttps%253A%252F%252Fappengine.google.com%252F_ah%252Fconflogin%253Fcontinue%253Dhttps%25253A%25252F%25252Fibrowser-ptest-495.appspot.com%25252Fapp%26shdf%3DCiQLEgZhaG5hbWUaGGlCcm93c2VyIEpMIFByaW1hcnkgVGVzdAwSAmFoIhSRyogabh24Wb9qiBOWWYDcgHPKCygBMhSlemvtQoyh86E3rLMozUQ5vvnNiQ%26service%3Dah%26ltmpl%3Dgm";
	//public static final String PRODUCTION_LOGIN = "https://johnlewislogin.appspot.com/loginpageservlet?DomainName=4jT7iswUCQJ%2BtWNzJ1iNtg%3D%3D&SAMLRequest=fVLJTsMwEL0j8Q%2BW79nKKqsJKiBEJZaIBg7cXGecmDp28TgN%2FD1uCgIOcPTz81vGMz176zTZgENlTU6zOKUEjLC1Mk1OH6ur6JSeFft7U%2BSdXrNZ71vzAK89oCfhpUE2XuS0d4ZZjgqZ4R0g84ItZrc3bBKnbO2st8JqSuaXOVWNllpYqZTUUqxELYWARtRGt2LZrpoWlvVSdZKSp69Yk22sOWIPc4OeGx%2BgNDuM0oMoPamyCTs4ZtnRMyXlp9O5MrsG%2F8Va7kjIrquqjMr7RTUKbFQN7i6wc9pY22iIhe229iVHVJsAS64RKJkhgvMh4IU12HfgFuA2SsDjw01OW%2B%2FXyJJkGIb4WybhyYttjYZBYTjH%2FSrhAmkxjpeNDd2Puf6fn3%2F50%2BLbYZr8kCo%2Bv23bZn5ZWq3EO5lpbYcLB9yHKt71ocmVdR33f7tlcTYiqo7kSGW9wTUIJRXUlCTFzvX3foSt%2BQA%3D&RelayState=https%3A%2F%2Faccounts.google.com%2FCheckCookie%3Fcontinue%3Dhttps%253A%252F%252Fappengine.google.com%252F_ah%252Fconflogin%253Fcontinue%253Dhttps%25253A%25252F%25252Fibrowser-pprod-501.appspot.com%25252Fapp%26shdf%3DCiQLEgZhaG5hbWUaGGlCcm93c2VyIEpMIFByaW1hcnkgVGVzdAwSAmFoIhSRyogabh24Wb9qiBOWWYDcgHPKCygBMhSlemvtQoyh86E3rLMozUQ5vvnNiQ%26service%3Dah%26ltmpl%3Dgm";
	//public static final String PRODUCTION_LOGIN = "https://jlp-login.appspot.com/user/login/accountlogin?param=H4sIAAAAAAAAAF1R2a6jRhT8F0uTF2euzWJjJrIijDFmt1kNLxG0m2ZvlgZfPMq%2Fh5mRoijSeSmdWqSq7ytHMHQbdiMcyOrbKvV11R2Ml6Rvi8N9HXB8o2ndoGr5SVKjWDkhDjxkCOpzSx9cZG7O1CgixQI3932gJtl4U9z%2B7UZIMivX278tzZMKXaSMkrKu%2B7Hfqw68pITz1s7z0ZsRR01vQTvwWEyz1pPox%2Bhsn2wUFaiLWHuLDqxCoh4wyUlrEoseyEHtnDF%2BWH5gVlUbdpHred2npIeBYBNbJrQe9S0Oqsl3npHmtHs%2BLGl6sALlBlgLyp9rZJ5HY4sNfIvrWXQHNiKNbcyPquWtndEP8ubgx1xZKH03doXRc7bracnlbnL5%2FgX4NqTpXMkgTfO5f%2FXVQXD2rK2pZYYmI2MVj6KvKbqMAu0M5%2BK1paxgvXnMqlrKSsIGc5LNISFFGJ1Ct7hunD4T6vKzaKF5ftK3Md2nDbPZbdd6Ekb9oRTxeqKZJGp2UdAxkrWr2iQE%2BomfrxrhKAxq%2F2kzTMqRCrhh3mGudOSAf7meotoPrxLdy3t6MCl2yPouHFe%2Fr2xYxbNDYgKXvTNC2uHbZhMDgMeGDB8IY1TBD4DrjZhBsOTjMod%2FAtyQvBnh8V9B28IG5Q38r%2BKvONsszLTCy%2Bd%2Fmi%2BM8IW%2BLJcnPX4NsP%2Fatj1%2Bft1tqY%2FFbGgx%2BeGxEBb0W1y3fwzZMz2K%2BV2XUJTF8i5LAi%2BW5UoENc8A2p8VqTWUy2mOAyoDTYl82X8%2FhZcj1BesZI49YxQnGc0GCd%2FlJysIwjNA15smzuhkZE4F64nc8Zwd9hLT6wZ%2Be%2FfdNDVmfv%2BVD%2FspB%2FAYZz9hReq2OqJ6qfCM6zhvzLj%2BUSFbuFw%2BvDzxrq5JYL5VKjcJOi5V%2F%2F0PARs2v14DAAA%3D";
	public static final String PRODUCTION_LOGIN = "https://jlp-login.appspot.com/user/login/accountlogin?param=H4sIAAAAAAAAAF1R2a6jRhT8F0uTF2euzWJjJrIijDFmt1kNLxG0m2ZvlgZfPMq%2Fh5mRoijSeSmdWqSq7ytHMHQbdiMcyOrbKvV11R2Ml6Rvi8N9HXB8o2ndoGr5SVKjWDkhDjxkCOpzSx9cZG7O1CgixQI3932gJtl4U9z%2B7UZIMivX278tzZMKXaSMkrKu%2B7Hfqw68pITz1s7z0ZsRR01vQTvwWEyz1pPox%2Bhsn2wUFaiLWHuLDqxCoh4wyUlrEoseyEHtnDF%2BWH5gVlUbdpHred2npIeBYBNbJrQe9S0Oqsl3npHmtHs%2BLGl6sALlBlgLyp9rZJ5HY4sNfIvrWXQHNiKNbcyPquWtndEP8ubgx1xZKH03doXRc7bracnlbnL5%2FgX4NqTpXMkgTfO5f%2FXVQXD2rK2pZYYmI2MVj6KvKbqMAu0M5%2BK1paxgvXnMqlrKSsIGc5LNISFFGJ1Ct7hunD4T6vKzaKF5ftK3Md2nDbPZbdd6Ekb9oRTxeqKZJGp2UdAxkrWr2iQE%2BomfrxrhKAxq%2F2kzTMqRCrhh3mGudOSAf7meotoPrxLdy3t6MCl2yPouHFe%2Fr2xYxbNDYgKXvTNC2uHbZhMDgMeGDB8IY1TBD4DrjZhBsOTjMod%2FAtyQvBnh8V9B28IG5Q38r%2BKvONsszLTCy%2Bd%2Fmi%2BM8IW%2BLJcnPX4NsP%2Fatj1%2Bft1tqY%2FFbGgx%2BeGxEBb0W1y3fwzZMz2K%2BV2XUJTF8i5LAi%2BW5UoENc8A2p8VqTWUy2mOAyoDTYl82X8%2FhZcj1BesZI49YxQnGc0GCd%2FlJysIwjNA15smzuhkZE4F64nc8Zwd9hLT6wZ%2Be%2FfdNDVmfv%2BVD%2FspB%2FAYZz9hReq2OqJ6qfCM6zhvzLj%2BUSFbuFw%2BvDzxrq5JYL5VKjcJOi5V%2F%2F0PARs2v14DAAA%3D";
//    public static final String TEST_LOGIN = "https://jlp-login.appspot.com/user/login/accountlogin?param=H4sIAAAAAAAAAF1R2a6jRhT8F0uTF2cuZrGBiawIY4zZbVbDSwRtaPZmafDFo%2Fx7mBkpiiKdl1KdqpKqvm8cwdDttJ%2FSEW%2B%2BbTJfV93ReEn6ruTu24DlW03rR1UrTpIaxcoJsuAhp6A5dxTnQpM4k5MIFQvc3DdHzrLxJtnD242gZNaud3hbmieVukgaFWldD9NwUJ30kmHW2zrPx2BGLDm%2FBY3jkZjlnSdRj8nZPZkoKmEfMfYOcoyCowHQyUlrE4saMaf2zhQ%2FLD8w67oL%2B8j1vP5T0sNAsLEtY0qPhg4F9ew7z0hzugMfVhQ1WoFyA4yVyp9baJ4nY4cMdIubRXRHJsKtbSyPuuOtvTGMMsH5MVuVytBPfWkMrO16WnK5m2xxeAG%2BCymqUPKUovjCv%2FrqKDgHxtbUKoezkTOKR1LXDF4mgXLGc%2FnakVawJR6LqlaykjDBkuRLiHEZRqfQLa%2BEM%2BRCU32WXWqen9Rtyg5ZSxP73VZPwmjgKhFtZ4pOonYfBT0tWfu6S0Kgn%2FjlqmGWRKDxnzZNZyyugRsWPWIrRw74l%2Bspqv3watG9vOcHnSEHb%2B%2FCcfP7xk7reHFwjNN17xzjbvxGEDEAaGrx%2BAERgnX6AVBDiHkK1nxUFemfALW4aKf0%2BK%2Bg69IWFm36X8VfcU6sn1mNVuZ%2Fmi%2B08IW6rFc0HRqLFn7NPr8y3P5jdRo7hH8YrOyKfoub7o8xf2ZHsbjrEozyWN7nSeDFslyLoOFpQPmLInWGcjktcUDmoK2gL%2Fvvp%2FByhOaClNyxFwTjJKeYIOH74mQFQXgG8HrTxAWejNyp02bGd7Tk3EGiB91Ab%2B%2B%2Bn%2BfWLO6%2F8tNhLkB6jPOfsMZNVx9hs%2FZ3Rk1ctGbc%2FOiPKV22GF%2BeeFe3ODDfKlmYGB7Xnv%2F%2BBzHtdEFbAwAA";
    public static final String DEV_LOGIN = "https://jlp-login.appspot.com/user/login/accountlogin?param=H4sIAAAAAAAAAF1R2a6jRhT8F0uTF2euzWJjJrIijDFmt1kNLxG0m2ZvlgZfPMq%2Fh5mRoijSeSnVqSqp6vvKEQzdht0IB7L6tkp9XXUH4yXp2%2BJwXwcc32haN6hafpLUKFZOiAMPGYL63NIHF5mbMzWKSLHAzX0fqEk23hS3f7sRkszK9fZvS%2FOkQhcpo6Ss637s96oDLynhvLXzfPRmxFHTW9AOPBbTrPUk%2BjE62ycbRQXqItbeogOrkKgHTHLSmsSiB3JQO2eMH5YfmFXVhl3kel73KelhINjElgmtR32Lg2rynWekOe2eD0uaHqxAuQHWgvLnGpnn0dhiA9%2FiehbdgY1IYxvzo2p5a2f0g7w5%2BDFXFkrfjV1h9JztelpyuZtcvn8Bvg1pOlcySNN87l99dRCcPWtrapmhychYxaPoa4ouo0A7w7l4bSkrWG8es6qWspKwwZxkc0hIEUan0C2uG6fPhLr8LFponp%2F0bUz3acNsdtu1noRRfyhFvJ5oJomaXRR0jGTtqjYJgX7i56tGOAqD2n%2FaDJNypAJumHeYKx054F%2Bup6j2w6tE9%2FKeHkyKHbK%2BC8fV7ysbVvHskJjAZe%2BMkHb4ttnEAOCxIcMHwhhV8APgeiNmECz5uMzhnwA3JG9GePxX0LawQXkD%2F6v4K842y2da4YX5n%2BYLI3yhL8uB4Wue9Pg1wP7rE04fi9PQYvLDYGEX9Ftct38M2TM9ivldl1CUxfIuSwIvluVKBDXPANqfFak1lMtpjgMqA02JfNl%2FP4WXI9QXrGSOPWMUJxnNBgnf5ScrCMIzQNebJs7oZGROBeuJ3PGcHfYS0%2BsGfnv33TQ1Zn7%2FlQ%2F7KQfwGGc%2FYUXqtjqieunvjOs4b8y4%2FtEfW7hcPrw88a6uSWC%2BVSo3CTouPf%2F9D1KNPkpbAwAA";
    public static final String PRIMARY_TEST_LOGIN = "https://jlp-login.appspot.com/user/login/accountlogin?param=H4sIAAAAAAAAAF1S2a6jRhD9F0uTF2euzWJjJrIijDFmt1kNLxG0m2ZvlgZfPMq%2Fh5mRoihSvRzVWaRT9X3lCIZuw26EA1l9W6W%2BrrqD8ZL0bXG4rwOObzStG1QtP0lqFCsnxIGHDEF9bumDi8zNmRpFpFjg5r4P1CQbb4rbv90ISWblevu3pXlSoYuUUVLWdT%2F2e9WBl5Rw3tp5Pnoz4qjpLWgHHotp1noS%2FRid7ZONogJ1EWtv0YFVSNQDJjlpTWLRAzmonTPGD8sPzKpqwy5yPa%2F7lPQwEGxiy4TWo77FQTX5zjPSnHbPhyVND1ag3ABrQflzjczzaGyxgW9xPYvuwEaksY35UbW8tTP6Qd4c%2FJgrC6Xvxq4wes52PS253E0u378A34Y0nSsZpGk%2B96%2B%2BOgjOnrU1tczQZGSs4lH0NUWXUaCd4Vy8tpQVrDePWVVLWUnYYE6yOSSkCKNT6BbXjdNnQl1%2BFi00z0%2F6Nqb7tGE2u%2B1aT8KoP5QiXk80k0TNLgo6RrJ2VZuEQD%2Fx81UjHIVB7T9thkk5UgE3zDvMlY4c8C%2FXU1T74VWie3lPDybFDlnfhePq95UNq3h2SEzgcu%2BMkHb4ttnEAOCxIcMHwhhV8APgeiNmECz5uMzhnwA3JG9GePxX0LawQXkD%2F6v4K842CzOt8LL5n%2BYLI3yhL8vkSY9fA%2By%2FtmT5ua8sv%2FtYzIYWkx8eC2FBv8V1%2B8eQPdOjmN91CUVZLO%2ByJPBiWa5EUPMMoP1ZkVpDuZzmOKAy0JTIl%2F33U3g5Qn3BSubYM0ZxktFskPBdfrKCIDwDdL1p4oxORuZUsJ7IHc%2FZYS8xvW7gt3ffTVNj5vdf%2BbCfcgCPcfYTVqRuqyOqlwrPuI7zxozrHxWyhcvlw8sT7%2BqaBOZbpXKToONS9d%2F%2FAF%2F4B%2BJeAwAA";
    public static final String TEST_LOGIN = "https://johnlewislogindev.appspot.com/user/login/accountlogin?param=H4sIAAAAAAAAAF1RW8%2BiSBT8LyYzL858iIrATMyGmygiiCAXXyZN00DLVRrkstn%2FvsxOMplscl4qdapOqs7fC1u46Df06hBpF98WsavpDt%2F3is742ycfvDs0SNLtFQYnTzHrsyVd1rwhhTSTZw9KefJOqi2XVDMFZ0GgeyqmLvzQmBylkpXKKY6Zv%2Fvbo57EHDBiQfvcOd4ZTZnkS19rR1erNt2gEt28knMs2Lg31VybBO5wZHvXSc82MU4CBrLyKF1KW7GOf3WQCBBkioG%2FbySGDp8NP6yA9riLsr0CrDKFLeSMUPK72qONYLiVb5p5ljcGN0GmegdyXlXBiQnekybzOtfxdIyPJmTUmEKAoFLdWWVLuINH2MdB2WZR5K5KGtLXq4Av663I4W4pRZBZq66cT%2BTMZUqwvYWve7FVh4Mos0pixOaqkIWQk64vU645wCbdIO6UPFs77nWn1VetOSnDJuwi2hHSm91H5dOssRZPmPhw1VAhG8GIUYnqNztG143NvfN1es7ZeNyrpo98MHbtxndaK3gEGpWkbZPTG%2FLsu4hgH%2FYnI8vYO%2BcmLwsd9Lsue26AfIdCtj5Cqt%2FvF18WN5SD0W5Bi%2Ba%2Fp21bk28UBSCsujn%2FR1JVSY4%2BYFVQUopgJlVVhtFfsCpbXHZo%2F1tQ16hMcIn%2BVPwAKTVvxnk1M%2F%2FTfNoIn9aHeXBRVwSXydd4%2BLrlmI%2FZidRV%2B9NgZmf0GRT1d5JG8V7Clq4kjxSoTBp6d6CquQQLfgPX7nhS6svpII7Ao1NYZomrulMk9LZQHKpTat%2FGKgFhut56If%2FCoul5gQyT4%2FUsjYl4Se0cFe%2FWqsaU2ymbRr9U091i3u%2FSwNav%2B6h5Y4j2IP0P5m1R5%2FukmPuTqwLg0gDFz%2F5eqdUxxTHtwgfr8NlkWfcHji5oeQwEctXYxZd%2F%2FgVwkltOawMAAA%3D%3D";
	
	public static final String DEFAULT_DELIMITER = ",";
	public static final String DELIMITER = "|";
	public static final int SHARD = 1;
	
	public static final String DEV_PROJECT_NAME = "cs-ibrowser-dev";
	public static final String TEST_PROJECT_NAME = "imposing-fx-485";
	public static final String PRIMARY_TEST_PROJECT_NAME = "ibrowser-ptest-495";
	public static final String PRODUCTION_PROJECT_NAME = "ibrowser-pprod-501";
	
	public static String DEV_INCOMING_BUCKET = "ibrowser_incoming";
	public static String DEV_REPORTS_BUCKET = "ibrowser_reports";
	public static String DEV_DELETED_BUCKET = "ibrowser_deleted";
	public static String DEV_ERRORS_BUCKET = "ibrowser_errors";
	public static String DEV_GCS_BUCKET_NAME = "ibrowser_audit_log";
	
	public static String TEST_INCOMING_BUCKET = "test_ibrowser_incoming";
	public static String TEST_REPORTS_BUCKET = "test_ibrowser_reports";
	public static String TEST_DELETED_BUCKET = "test_ibrowser_deleted";
	public static String TEST_ERRORS_BUCKET = "test_ibrowser_errors";
	public static String TEST_GCS_BUCKET_NAME = "test_audit_log";
	
	public static String PRIMARY_TEST_INCOMING_BUCKET = "ptest_ibrowser_incoming";
	public static String PRIMARY_TEST_REPORTS_BUCKET = "ptest_ibrowser_reports";
	public static String PRIMARY_TEST_DELETED_BUCKET = "ptest_ibrowser_deleted";
	public static String PRIMARY_TEST_ERRORS_BUCKET = "ptest_ibrowser_errors";
	public static String PRIMARY_TEST_GCS_BUCKET_NAME = "ptest_audit_log";
	
	
	public static String PRODUCTION_INCOMING_BUCKET = "prod_ibrowser_incoming";
	public static String PRODUCTION_REPORTS_BUCKET = "prod_ibrowser_reports";
	public static String PRODUCTION_DELETED_BUCKET = "prod_ibrowser_deleted";
	public static String PRODUCTION_ERRORS_BUCKET = "prod_ibrowser_errors";
	public static String PRODUCTION_GCS_BUCKET_NAME = "prod_audit_log";
	
	
	
	public static final String DEV_SERVICE_ACCOUNT_ID = "521712190652-n9g0so81srhf4k4s10h7rr6mdk3u9v6j@developer.gserviceaccount.com";
	public static final String DEV_SERVICE_ACCOUNT_PKCS12_FILE_PATH = "5d90ba089906de3750d144540e4404dd52462474-privatekey.p12";
	
	public static final String TEST_SERVICE_ACCOUNT_ID = "759069253207-qmd7h8n26ukgkfggq68elt002jvdpngd@developer.gserviceaccount.com";
	public static final String TEST_SERVICE_ACCOUNT_PKCS12_FILE_PATH = "f376aa952804dda13a754130eeacf33703214ac6-privatekey.p12";
	
	public static final String PRIMARY_TEST_SERVICE_ACCOUNT_ID = "37112205041-e23ta1hl5l235s5f4l1245vs9ejgmi04@developer.gserviceaccount.com";
	public static final String PRIMARY_TEST_SERVICE_ACCOUNT_PKCS12_FILE_PATH = "99a79e8c65e5e299c7d8f9e46f36112e87fc568f-privatekey.p12";
	
	public static final String PRODUCTION_SERVICE_ACCOUNT_ID = "111682412906-8g3rhnvvhqac46l6tuvvpnjl4u8aicdk@developer.gserviceaccount.com";
	public static final String PRODUCTION_SERVICE_ACCOUNT_PKCS12_FILE_PATH = "bec29ea9ff8630480fc31c4951b718478d051e13-privatekey.p12";
	
	public static String DEV_ACCOUNT_USER = "cloudsherpas.dev@johnlewis.co.uk";
	public static String TEST_ACCOUNT_USER = "cloudsherpas.dev@johnlewis.co.uk";
//	public static String TEST_ACCOUNT_USER = "Ash@test.johnlewis.co.uk";
	public static String PRIMARY_TEST_ACCOUNT_USER = "cloudsherpas.dev@johnlewis.co.uk";
	public static String PRODUCTION_ACCOUNT_USER = "cloudsherpas.dev@johnlewis.co.uk";
//	public static String PRODUCTION_ACCOUNT_USER = "ibrowser@johnlewis.co.uk";
	
	/* PRIMARY TEST */
	/*
    public static final String SERVICE_ACCOUNT_ID = PRIMARY_TEST_SERVICE_ACCOUNT_ID;
	public static final String SERVICE_ACCOUNT_PKCS12_FILE_PATH = PRIMARY_TEST_SERVICE_ACCOUNT_PKCS12_FILE_PATH;
	public static final String ACCOUNT_USER = PRIMARY_TEST_ACCOUNT_USER;
	public static final String PROJECT_NAME = PRIMARY_TEST_PROJECT_NAME;
	public static final String INCOMING_BUCKET = PRIMARY_TEST_INCOMING_BUCKET;
	public static final String REPORTS_BUCKET = PRIMARY_TEST_REPORTS_BUCKET;
	public static final String DELETED_BUCKET = PRIMARY_TEST_DELETED_BUCKET;
	public static final String ERRORS_BUCKET = PRIMARY_TEST_ERRORS_BUCKET;
	public static final String GCS_BUCKET_NAME = PRIMARY_TEST_GCS_BUCKET_NAME;
	public static final String LOGIN = PRIMARY_TEST_LOGIN;
	public static String MESSAGE_OF_THE_DAY_IMAGES = "ptest_message_of_day_images";
	
	
	public static final String DOMAIN = "johnlewis.co.uk";
	public static final String FROM_EMAIL = "cloudsherpas.dev@johnlewis.co.uk";
	public static final String TO_EMAIL = "cloudsherpas.dev@johnlewis.co.uk,JL_Information_Systems@johnlewis.co.uk";
	*/
	
	
	/* PRODUCTION */
//	/*
	
	public static final String SERVICE_ACCOUNT_ID = PRODUCTION_SERVICE_ACCOUNT_ID;
	public static final String SERVICE_ACCOUNT_PKCS12_FILE_PATH = PRODUCTION_SERVICE_ACCOUNT_PKCS12_FILE_PATH;
	public static final String ACCOUNT_USER = PRODUCTION_ACCOUNT_USER;
	public static final String PROJECT_NAME = PRODUCTION_PROJECT_NAME;
	public static final String INCOMING_BUCKET = PRODUCTION_INCOMING_BUCKET;
	public static final String REPORTS_BUCKET = PRODUCTION_REPORTS_BUCKET;
	public static final String DELETED_BUCKET = PRODUCTION_DELETED_BUCKET;
	public static final String ERRORS_BUCKET = PRODUCTION_ERRORS_BUCKET;
	public static final String GCS_BUCKET_NAME = PRODUCTION_GCS_BUCKET_NAME;
	public static final String LOGIN = PRODUCTION_LOGIN;
	
	public static String MESSAGE_OF_THE_DAY_IMAGES = "prod_message_of_day_images";
	public static final String DOMAIN = "johnlewis.co.uk";
	public static final String FROM_EMAIL = "cloudsherpas.dev@johnlewis.co.uk";
	public static final String TO_EMAIL = "cloudsherpas.dev@johnlewis.co.uk,JL_Information_Systems@johnlewis.co.uk"; 
//	*/
	
	/* TEST - no longer in use */
	/*
	public static final String SERVICE_ACCOUNT_ID = TEST_SERVICE_ACCOUNT_ID;
	public static final String SERVICE_ACCOUNT_PKCS12_FILE_PATH = TEST_SERVICE_ACCOUNT_PKCS12_FILE_PATH;
	public static final String ACCOUNT_USER = TEST_ACCOUNT_USER;
	public static final String PROJECT_NAME = TEST_PROJECT_NAME;
	public static final String INCOMING_BUCKET = TEST_INCOMING_BUCKET;
	public static final String REPORTS_BUCKET = TEST_REPORTS_BUCKET;
	public static final String DELETED_BUCKET = TEST_DELETED_BUCKET;
	public static final String ERRORS_BUCKET = TEST_ERRORS_BUCKET;
	public static final String GCS_BUCKET_NAME = TEST_GCS_BUCKET_NAME;
	public static final String LOGIN = TEST_LOGIN;

	public static String MESSAGE_OF_THE_DAY_IMAGES = "test_message_of_day_images";
//	public static final String DOMAIN = "dev.johnlewis.co.uk";
//	public static final String DOMAIN = "test.johnlewis.co.uk";
	public static final String DOMAIN = "johnlewis.co.uk";
	public static final String FROM_EMAIL = "cloudsherpas.dev@johnlewis.co.uk";
	public static final String TO_EMAIL = "cloudsherpas.dev@johnlewis.co.uk,JL_Information_Systems@johnlewis.co.uk";
//*/		
    
	/* DEV - no longer in use */
	/*public static final String SERVICE_ACCOUNT_ID = DEV_SERVICE_ACCOUNT_ID;
	public static final String SERVICE_ACCOUNT_PKCS12_FILE_PATH = DEV_SERVICE_ACCOUNT_PKCS12_FILE_PATH;
	public static final String ACCOUNT_USER = DEV_ACCOUNT_USER;
	public static final String PROJECT_NAME = DEV_PROJECT_NAME;
	public static final String INCOMING_BUCKET = DEV_INCOMING_BUCKET;
	public static final String REPORTS_BUCKET = DEV_REPORTS_BUCKET;
	public static final String DELETED_BUCKET = DEV_DELETED_BUCKET;
	public static final String ERRORS_BUCKET = DEV_ERRORS_BUCKET;
	public static final String GCS_BUCKET_NAME = DEV_GCS_BUCKET_NAME;
	public static final String LOGIN = DEV_LOGIN;
	
	public static final String DOMAIN = "dev.johnlewis.co.uk";
	public static final String FROM_EMAIL = "cloudsherpas.dev@johnlewis.co.uk";
	public static final String TO_EMAIL = "cloudsherpas.dev@johnlewis.co.uk,JL_Information_Systems@johnlewis.co.uk";*/
	
	
	/**
	 * TODO this is for only test IP ranges
	 */
	public static final List<IPRangeItem> IP_RANGES = Arrays.asList(
														new IPRangeItem("193.35.248.1", "193.35.255.254"), // JL IP ranges
														new IPRangeItem("86.160.18.1", "86.160.18.254"),
														new IPRangeItem("83.221.179.1", "83.221.179.254"),
														new IPRangeItem("86.164.216.151", "86.164.216.151"),
														new IPRangeItem("86.160.16.1", "86.160.16.254"),
														new IPRangeItem("38.104.182.34", "38.104.182.34"),
														new IPRangeItem("195.158.26.1", "195.158.26.254"),
														new IPRangeItem("125.252.68.90", "125.252.68.94"),
														new IPRangeItem("103.21.170.52", "103.21.170.52"),
														new IPRangeItem("10.20.34.54", "10.20.34.54"),
														new IPRangeItem("127.0.0.1", "127.0.0.1")
														);
}

