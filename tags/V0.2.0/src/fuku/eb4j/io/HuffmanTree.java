package fuku.eb4j.io;

import java.util.Hashtable;
import java.util.Vector;

import net.cloudhunter.bb.EBLogger;
import net.cloudhunter.compat.java.lang.Comparable;
import net.cloudhunter.compat.java.util.ArrayList;
import net.cloudhunter.compat.java.util.Collections;
import net.cloudhunter.compat.java.util.List;
import net.rim.device.api.system.EventLogger;
import fuku.eb4j.util.HexUtil;

/**
 * ハフマン木を作成するために通常の選択ソートを行うと、ループ回数が多すぎてパフォーマンスが悪い。
 * それを改善するために、処理方法を工夫したクラス。
 * 
 * @author Cloud Hunter
 */
public class HuffmanTree {
	/** HuffmanNodeのVector */
	private Vector _nodes = null;
	
	/** ソート（大->小）に並べられたfreqのVector */
	private Vector _sortedFreqs = null;
	
	/** 処理用<freq, そのfreqを持つHuffmanNode群のVector> */
	private Hashtable _fresNodesMap = null;	//
	
	/**
	 * コンストラクタ
	 */
	public HuffmanTree() {
		this(0);
	}

	/**
	 * コンストラクタ。
	 * 
	 * @param initialCapacity 初期サイズ
	 */
	public HuffmanTree(int initialCapacity) {
		_nodes = new Vector(initialCapacity);
		_sortedFreqs = new Vector();
		_fresNodesMap = new Hashtable();
	}

	/**
	 * 対象ノードを追加する。
	 * @param node
	 */
	public void add(HuffmanNode node) {
		addIndex(node);
		addSortedFreqs(node);
	}

	/**
	 * 選択ソートを行う。
	 */
	private void doSelectionSort() {
		for(int i=0; i<_nodes.size(); i++) {
			Integer freqToBePlaced = null;
			Vector list = null;
			while(true) {
				freqToBePlaced = (Integer)_sortedFreqs.elementAt(0);
				list = (Vector)_fresNodesMap.get(freqToBePlaced);
				if(list.size() > 0) {
					break;
				}
				_sortedFreqs.removeElementAt(0);
			}
			
			HuffmanNode currentNode = (HuffmanNode)_nodes.elementAt(i);
			HuffmanNode nextNode = getMinimumIndexNode(list);
			
			if(currentNode != nextNode) {
				//swap
				Collections.swap(_nodes, i, nextNode.getIndex());
				currentNode.setIndex(nextNode.getIndex());
			}
		}
	}
	
//	private static Hashtable sortedCache = new Hashtable() {
//		{
//			put("0000A2220000A22100007507000075070000664900004F110000300D00002BBB00002B1D00002217",
//				 "0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,1030,1028,15,16,1026,1027,1029,1031,17,1025,18,19,20,21,1024,22,23,24,25,26,27,29,28,30,31,32,33,34,35,36,37,1060,38,39,1057,40,42,41,43,1088,1062,44,45,46,1090,1083,1096,47,1100,1080,48,1098,1074,1078,49,1084,50,51,1076,1061,1102,52,1072,1082,53,1086,1094,1092,1079,1077,54,1085,1097,55,1081,1095,1089,56,57,58,59,60,1101,61,1032,1059,1073,1075,62,1093,1091,63,64,65,1040,66,1099,67,69,68,1042,70,1087,71,72,1044,1046,73,74,1038,76,1048,75,1152,77,78,1138,79,80,81,82,83,84,1058,1142,1052,85,86,1103,1124,1036,1054,1134,1050,1034,87,88,1112,1136,1110,1126,1128,1150,89,90,1104,1122,1108,1130,1064,1132,1216,1068,1140,92,91,1114,1106,93,1049,1070,1144,1043,1041,1047,1045,94,1204,1210,1148,1206,95,1202,1218,1224,1212,1066,1120,1208,1222,96,97,1220,1214,98,1185,1200,1118,1105,99,1116,1139,100,1141,101,1143,1129,1146,1121,103,102,104,1137,105,1123,1186,1039,1135,1053,1051,106,1198,1149,1056,1113,1071,1115,107,1119,1109,1131,108,109,1117,1158,1133,110,111,1156,112,1107,113,114,1205,1225,1176,1213,1221,115,1172,1207,1211,1215,1063,1168,1203,1217,1223,1145,1174,1209,1219,1170,1154,116,1160,118,117,1111,1127,119,120,1180,1201,121,1276,1065,122,1250,123,1178,1254,1262,124,1067,1162,1252,1272,1125,1184,1264,1258,1268,1194,1196,1226,1270,1274,1278,125,126,1166,1182,1256,1266,1190,1164,1192,1188,1260,1199,127,128,1069,129,130,131,1147,132,133,1187,135,134,137,136,138,139,1234,1236,1246,1240,140,141,1244,142,1230,143,145,148,146,144,147,1248,1228,1232,1242,149,150,151,1238,152,1033,153,154,155,156,157,1157,158,159,160,161,164,162,163,165,167,166,169,168,170,171,172,173,174,175,178,177,176,179,1155,180,181,184,182,183,186,185,187,1055,1175,188,1159,190,189,191,192,1177,193,1153,1161,1173,194,1169,1171,195,196,1197,197,1253,1275,1179,1183,198,1273,1279,199,1163,1181,200,1261,1277,1151,1189,1195,1267,1037,1265,1271,1255,1259,1269,1263,1191,201,204,203,202,1193,205,1251,1257,206,1167,1035,207,208,1165,209,210,213,211,212,214,215,216,217,218,219,221,"
//				+"222,220,224,223,225,227,226,228,230,229,232,231,233,234,235,236,1249,237,1247,238,239,241,240,1231,1239,242,243,1227,1229,1235,1237,244,1233,1241,1243,246,245,1245,247,248,249,250,253,254,251,252,255,256,257,258,259,263,264,260,261,262,265,266,269,268,270,267,271,275,272,273,274,276,279,277,278,281,282,280,284,285,283,286,287,288,289,290,291,293,294,292,296,295,298,299,300,297,301,304,302,303,305,311,306,307,310,308,309,318,319,316,317,313,312,315,314,323,320,321,322,330,328,329,324,326,327,325,336,339,337,331,340,332,333,335,338,334,342,343,344,341,345,346,348,347,356,351,353,352,358,355,357,349,354,350,363,364,365,359,360,361,362,373,378,379,367,371,380,375,377,368,376,366,369,370,374,372,381,382,386,384,394,387,391,383,388,385,397,395,393,389,392,390,396,400,410,411,401,404,405,408,409,406,402,412,403,398,413,407,399,431,421,427,422,416,423,417,424,428,429,418,414,425,432,430,419,415,426,420,440,449,450,441,444,442,447,451,448,438,443,439,445,434,446,435,437,436,433,458,464,453,459,462,460,455,463,456,461,454,457,452,467,468,472,473,475,469,478,474,476,477,479,465,470,466,471,480,482,486,490,484,487,491,485,492,493,488,489,483,481,507,500,496,503,501,506,504,505,497,494,495,498,499,502,514,516,515,508,517,511,512,509,513,510,518,523,524,521,519,522,520,527,528,532,529,530,531,525,526,533,539,536,537,540,534,535,541,542,543,538,544,545,548,546,549,547,558,561,562,563,553,554,555,551,552,556,557,550,559,560,568,569,570,571,564,572,565,573,574,566,567,576,580,581,577,578,579,575,582,583,584,585,597,587,588,589,590,586,591,592,593,594,595,596,605,606,598,599,607,608,600,609,610,601,602,603,604,622,623,624,625,626,615,616,617,618,619,611,620,621,612,613,614,627,628,629,630,631,632,633,634,635,637,638,639,640,641,642,643,644,645,646,647,648,649,650,651,636,654,655,656,657,658,659,660,652,653,670,671,661,662,663,664,665,666,667,668,669,672,673,674,675,676,677,678,679,680,681,682,683,684,685,708,689,690,709,721,722,723,691,710,711,712,713,692,693,694,714,715,716,717,695,"
//				+"696,718,719,720,697,698,699,700,701,702,703,704,705,706,707,686,687,688,736,737,750,751,764,724,752,753,725,738,739,726,754,727,728,729,740,730,731,741,732,765,733,742,743,766,767,768,755,734,735,756,769,757,744,745,746,758,759,760,747,761,762,763,748,749,782,806,800,783,789,807,790,791,801,792,775,784,785,802,786,776,787,803,808,809,788,793,794,804,810,811,795,774,777,770,771,772,778,779,773,780,781,796,805,797,798,799,820,821,833,834,822,831,835,836,823,824,825,826,827,828,832,812,829,813,837,814,830,815,816,817,818,819,841,846,847,838,843,848,849,850,851,852,853,854,842,844,839,845,840,862,863,875,864,873,868,869,859,856,872,857,865,871,860,874,867,866,858,870,861,855,877,878,879,883,880,881,882,876,885,886,884,899,900,892,901,887,902,893,894,903,895,904,905,896,897,888,890,891,898,889,914,919,920,918,911,912,906,915,907,916,909,910,908,917,913,921,930,931,932,933,934,922,923,924,929,925,926,927,928,935,946,942,947,944,948,936,949,943,951,950,945,937,938,939,940,952,941,953,964,967,968,980,965,969,955,960,958,966,981,961,970,959,954,971,963,972,973,962,974,956,975,976,977,978,957,979,985,986,989,994,1002,1003,984,987,999,997,1000,1006,992,983,1001,1007,990,993,1005,995,1004,982,996,991,998,988,1008,1011,1013,1022,1014,1009,1023,1017,1012,1015,1016,1010,1021,1018,1020,1019,1280"
//			);
//			put("0000EA5D0000D13900002B5900002B37000014ED0000104500000B3500000B0700000AFD000008FD",
//					 "0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,1119,26,27,1120,28,1118,29,1141,1117,1140,1139,1186,1163,1137,1121,1123,1116,1165,30,1135,1185,1138,31,32,1128,33,1122,1136,1124,1164,1187,1134,1127,1115,34,1130,1129,35,1162,1125,1132,1131,1166,1126,1133,1184,1142,1167,1188,1114,36,1168,1169,1113,1170,37,1183,1171,1189,1182,1172,1181,1173,38,39,1177,1112,1174,40,1180,1175,1178,41,1179,1176,1190,42,1111,43,1152,1143,1161,1149,1110,1151,1191,1153,44,45,46,47,1160,48,1145,1148,1146,1150,1144,1109,1155,1192,1156,1157,1158,49,1147,1154,50,52,51,53,1108,1159,54,55,1193,1107,1194,56,1106,57,1105,58,1195,59,60,61,62,63,1104,64,65,66,1196,67,68,69,1103,70,71,1197,72,1102,73,74,75,1198,1101,76,77,79,78,80,81,1199,1100,82,83,84,85,1099,1200,86,87,89,88,90,1098,91,1201,92,94,93,96,95,1097,97,98,100,99,1202,102,101,1096,103,104,105,106,107,1203,108,1095,109,110,111,112,1204,113,1094,114,115,116,117,118,119,1205,121,120,122,1093,123,124,125,126,127,1092,128,130,131,129,1206,133,132,134,135,1091,137,139,136,138,1207,140,141,142,143,144,1090,145,1208,147,148,146,149,150,151,152,1089,153,154,155,156,157,1209,158,159,160,1088,161,163,162,1210,164,165,166,167,168,169,170,171,172,1087,173,174,176,175,177,178,179,1211,181,183,182,180,1086,185,184,186,188,187,1212,1085,189,190,191,193,196,192,194,195,197,198,199,200,1084,201,203,202,1213,207,204,205,206,209,210,208,1214,213,212,211,1083,214,215,217,218,216,219,220,1082,223,221,222,1215,224,225,227,226,229,228,230,232,233,231,235,237,236,234,1216,1081,241,243,244,238,240,242,239,247,245,246,249,248,1217,251,250,1080,252,253,254,255,257,256,259,261,258,260,1079,262,265,263,266,264,269,270,271,267,268,1218,272,273,1078,275,277,279,274,276,278,282,281,283,280,1219,284,285,1077,291,287,290,292,288,289,286,296,297,299,294,298,293,295,1220,300,301,302,303,304,309,305,306,307,308,1076,312,313,310,311,314,315,1221,316,320,321,322,318,319,317,1075,325,323,324,329,330,331,326,327,328,1222,338,339,334,335,332,336,340,337,333,"
//					+"1074,345,348,342,343,346,344,347,341,1223,353,349,350,351,352,1073,358,362,354,359,360,355,361,356,357,368,369,370,364,365,366,371,363,367,1224,1072,373,374,375,376,377,378,379,372,384,381,385,382,380,383,386,1225,1071,389,387,396,390,391,392,388,393,394,397,395,400,401,402,403,398,399,1226,1070,414,412,406,407,404,413,408,409,410,405,411,428,421,426,415,416,427,422,417,418,419,423,424,420,425,1227,1069,429,430,431,432,433,434,435,1068,436,437,438,439,440,444,441,442,443,454,449,455,450,445,446,451,456,457,452,447,458,453,459,448,460,1228,469,462,463,464,470,465,466,467,468,461,1229,1067,473,480,474,481,482,475,476,477,483,471,484,485,478,486,472,479,1066,489,490,491,492,493,494,495,487,496,488,497,498,504,499,505,500,506,507,508,509,501,502,510,511,503,1230,1065,524,525,526,514,515,527,516,517,518,528,519,520,529,530,531,521,512,522,513,532,523,1231,541,542,543,544,545,533,546,534,535,536,537,538,539,540,1064,560,561,553,562,554,555,556,563,564,565,566,567,568,547,548,549,550,551,557,558,552,559,1232,1063,572,573,574,575,576,577,578,579,580,581,582,569,570,571,1233,595,587,596,597,588,598,599,589,590,591,592,583,593,584,594,585,586,1062,605,606,607,608,600,601,609,610,611,612,613,614,615,602,616,603,617,618,604,1234,1061,633,622,634,623,635,636,624,637,625,638,626,639,627,628,629,640,641,642,643,644,645,646,630,619,620,631,621,632,1235,1060,650,663,651,664,652,665,653,666,654,655,656,657,647,648,658,659,660,661,649,662,1059,677,678,679,680,681,682,667,668,683,669,670,684,685,686,687,688,671,689,672,673,690,691,692,674,675,676,1236,1237,1058,714,715,700,716,717,701,718,719,702,720,721,703,704,705,706,707,708,709,693,710,694,695,711,696,697,712,698,713,699,1238,738,739,740,741,742,722,723,743,744,724,725,726,745,746,727,728,747,729,730,748,731,749,732,733,734,750,735,751,736,737,1057,771,754,755,772,773,756,774,775,757,758,759,760,761,776,762,763,777,778,779,780,781,782,783,764,765,766,767,768,752,769,770,753,1239,1056,792,793,794,795,796,797,798,799,800,801,802,803,"
//					+"804,805,806,807,808,809,810,811,812,813,814,815,784,785,786,787,788,789,816,817,790,791,1240,1241,1055,843,822,844,823,845,824,825,846,826,827,847,848,828,829,849,830,850,851,831,832,833,834,835,836,837,838,839,840,818,841,842,819,820,821,1242,1054,872,873,874,875,876,877,878,879,880,881,882,883,884,885,852,886,887,888,853,854,855,856,857,858,859,860,861,862,863,889,864,865,866,890,891,867,868,892,893,894,895,896,897,898,899,900,869,901,870,871,1243,940,941,942,906,907,908,909,933,910,911,912,913,914,915,916,917,918,919,920,934,903,935,1024,936,921,922,923,924,937,925,904,926,927,928,929,930,938,931,939,905,932,902,1244,981,947,948,968,976,977,969,944,945,974,978,970,975,982,949,950,951,952,953,954,983,984,985,986,987,988,989,990,991,955,956,957,1004,992,979,993,994,967,980,958,959,995,996,997,943,960,961,998,999,971,962,963,1000,972,964,973,965,1001,946,966,1002,1003,1053,1245,1019,1012,1017,1007,1021,1013,1014,1022,1008,1020,1005,1023,1018,1006,1052,1015,1016,1009,1010,1011,1246,1051,1050,1247,1248,1049,1249,1250,1048,1251,1047,1252,1253,1045,1046,1254,1255,1044,1256,1257,1043,1041,1042,1258,1259,1260,1039,1040,1261,1262,1263,1036,1037,1038,1264,1265,1034,1035,1266,1267,1268,1269,1032,1031,1033,1270,1271,1272,1028,1030,1029,1273,1274,1026,1027,1025,1275,1276,1277,1278,1279,1280"
//			);
//			put("0000B30500000DE500000B030000092B00000803000007E70000066F0000066F000005B5000004AF",
//					 "0,1,2,1028,1030,1027,1025,1029,1026,3,1031,4,5,1024,6,7,8,9,10,11,12,13,14,15,16,17,18,20,19,21,22,23,24,25,26,27,28,29,1032,30,31,32,33,1040,34,35,36,37,1036,39,38,40,1038,41,1042,42,1034,43,1043,1039,1041,44,1033,1088,45,1037,1044,1035,46,47,48,1084,49,50,51,52,1087,1072,53,1080,1061,54,1100,1083,1078,1102,1090,55,1086,1076,1089,1096,1074,1060,1081,1098,56,57,1077,1082,1101,1092,1097,58,1093,59,1094,61,60,1085,62,64,63,1091,65,1279,1216,1079,66,67,1073,68,69,70,71,72,1095,74,73,75,1136,78,77,76,79,1152,81,80,82,83,1120,84,1099,85,87,1059,1075,86,89,88,1048,90,92,91,1122,1062,1108,1114,1126,1132,93,1124,1134,1103,1121,94,1150,1110,1128,1123,96,95,1118,1142,1057,1104,1112,1127,1064,1144,97,1111,1138,98,1056,1058,99,100,103,1068,101,102,105,104,106,1116,107,108,109,1105,110,1106,1140,1109,1139,111,1130,112,113,1129,1131,1135,114,1066,1113,1115,1148,115,1046,116,117,1176,121,119,118,1065,1070,120,122,123,1071,1117,1278,124,125,1119,1137,1107,1067,1069,126,1276,1063,1125,127,128,1133,1141,131,129,132,130,1143,1272,133,1222,134,1146,1248,136,135,1185,137,138,141,139,140,1168,144,142,143,1052,1055,145,1256,146,148,147,1190,150,149,1145,1147,1174,1228,1252,1262,152,151,1155,1164,153,1151,1172,1250,1045,1054,1154,1156,1049,154,1208,1264,1266,1196,1202,1268,1050,1188,1204,1236,1244,1153,1217,1258,155,1194,1270,156,157,158,1186,1192,1254,1047,159,1180,1240,1162,1166,1210,160,163,161,162,1159,1200,1214,1224,1234,1242,164,1149,1170,1219,1246,1230,1274,1158,1212,1178,1260,165,1184,1238,1160,1218,1231,166,1206,1251,170,167,169,168,1223,1273,171,172,1220,1267,174,175,176,177,173,1182,1226,178,179,1167,180,181,182,1215,1255,184,183,1249,185,186,187,1275,188,189,1265,192,190,193,191,1232,1277,194,196,200,197,198,199,195,1183,1198,201,205,203,204,202,1157,1247,207,208,209,206,1263,212,213,214,210,211,218,215,216,1171,217,220,221,219,1271,222,223,224,228,229,225,226,230,227,231,234,233,235,236,1177,1225,232,238,239,237,1161,1169,1187,1241,1051,242,240,243,1175,1213,241,248,244,"
//					+"245,249,246,247,1243,1261,1053,250,1179,1189,1201,251,252,1191,1227,1269,253,254,259,262,264,256,263,257,260,261,1193,1197,1209,255,1233,258,1253,1257,268,271,272,273,265,269,270,266,1163,1165,267,1173,1181,1199,1229,1259,275,276,277,278,1195,1207,1239,274,279,280,281,282,283,1211,1235,287,288,289,290,1221,284,285,286,295,291,292,293,1237,294,299,296,300,297,298,304,305,1203,301,302,303,307,315,312,308,316,309,313,310,311,314,306,317,324,331,332,318,319,320,325,328,329,326,321,322,330,327,323,341,333,334,335,336,337,338,339,340,1245,352,342,358,348,355,343,350,353,1205,359,349,356,344,346,357,354,345,351,347,361,367,362,368,363,364,369,365,360,370,366,371,381,393,386,389,378,394,387,390,391,372,382,373,374,383,375,379,380,376,384,377,388,385,392,403,404,396,405,406,397,398,402,399,407,411,408,409,410,400,395,401,412,423,424,418,419,413,425,420,421,426,427,414,415,416,417,422,440,437,438,441,439,428,442,443,444,445,433,429,446,430,431,434,435,436,432,447,468,469,470,471,461,462,454,455,448,463,456,449,464,472,450,457,458,465,451,459,466,452,460,467,453,488,476,489,477,490,491,487,492,481,473,482,493,494,478,495,479,483,496,497,480,498,499,484,500,474,501,475,485,486,504,528,505,506,507,520,521,508,529,522,509,510,511,512,513,514,515,516,523,524,517,525,518,526,502,527,503,519,530,543,549,550,535,536,544,551,531,545,552,537,553,538,546,532,554,555,556,557,533,539,540,534,547,548,541,542,558,559,572,560,573,561,574,575,576,587,588,577,578,589,562,590,579,580,581,582,563,583,564,565,584,566,567,585,568,586,569,570,571,616,597,617,618,619,620,621,598,599,600,601,602,603,604,591,605,606,607,592,608,609,622,623,593,610,611,594,612,595,596,613,624,614,615,668,651,652,669,653,628,654,629,630,627,631,632,670,633,634,635,655,671,672,656,657,658,659,636,637,660,661,673,638,639,674,662,640,675,676,677,678,641,642,643,644,645,663,664,646,647,665,648,649,666,625,626,650,667,684,703,704,705,685,706,686,687,707,729,688,689,708,730,731,709,690,710,711,691,692,732,693,733,694,734,712,"
//					+"735,713,736,714,695,696,715,716,717,737,718,679,680,738,739,740,697,741,681,719,720,698,721,699,722,742,723,724,700,725,726,682,683,701,727,728,702,758,847,787,816,759,788,817,760,789,761,762,818,819,790,763,764,848,765,766,791,767,792,849,768,769,820,821,850,822,851,770,823,793,852,824,825,853,826,827,786,794,854,855,795,828,796,797,798,856,829,830,771,799,772,831,773,800,774,801,832,775,743,833,834,744,776,745,835,746,836,777,802,747,778,748,803,804,837,779,805,749,838,806,839,807,750,751,808,780,752,809,840,810,781,841,753,842,754,811,812,782,843,783,784,844,785,755,813,814,815,845,756,846,757,911,881,966,912,967,968,938,939,882,940,883,969,913,941,914,915,916,942,970,884,917,971,972,918,973,974,975,919,976,920,943,977,885,978,886,887,944,979,888,945,946,980,857,981,947,948,921,982,858,983,889,890,984,985,986,859,987,891,922,923,860,949,988,950,892,951,893,952,894,953,954,955,924,956,861,957,862,863,895,958,959,960,864,989,865,925,990,896,866,867,868,961,869,870,991,926,897,962,871,872,927,898,873,899,874,900,901,992,875,902,963,876,877,993,928,903,878,964,929,904,935,905,879,906,907,965,930,936,931,908,937,909,910,932,880,933,934,994,995,996,1022,1015,1004,1008,1023,1005,1010,1018,1019,1000,1017,1006,1011,1012,1013,1016,1014,1002,1009,1003,1007,1001,1020,1021,999,997,998,1280"
//			);
//		}
//	};
	
	//--------------------------------------------------------------------------------
	// キャッシュ関連
	//--------------------------------------------------------------------------------
	
	/** HuffmanTree作成に利用するための、選択ソート済みVectorのキャッシュ　*/
	private static Hashtable huffmanCache = null;

	/** キャッシュをセットする */
	public static void setSortedCache(Hashtable cache) {
		huffmanCache = cache;	
	}
	
	/** 現在のキャッシュの内容を取得する */
	public static Hashtable getSortedCache() {
		return huffmanCache;
	}
	
	/**
	 * キャッシュのためのキー文字列を作成する。
	 * 
	 * @return キャッシュのためのキー文字列
	 */
	private String getCacheKey() {
		StringBuffer buf = new StringBuffer();
		for(int i=0; i<10; i++) {
			HuffmanNode node = (HuffmanNode)_nodes.elementAt(i);
			buf.append(HexUtil.toHexString(node.getFrequency(), 8));
		}
		return buf.toString();
	}
	
	/**
	 * ソート済み文字列からキャッシュ文字列を作成する。
	 * 
	 * @return キャッシュ文字列
	 */
	private String getCacheString() {
		StringBuffer buf = new StringBuffer();
		for(int i=0; i<_nodes.size(); i++) {
			if(i > 0) {
				buf.append(',');
			}
			buf.append(((HuffmanNode)_nodes.elementAt(i)).getOrgIndex());
		}
		return buf.toString();
	}
	
	/**
	 * キャッシュ済み文字列により選択ソートを実行する。
	 * 
	 * @param cacheString キャッシュ文字列
	 */
	private void sortByCacheString(String cacheString) {
		Vector sorted = new Vector();
		int offset=0;
		while(true) {
			int commaPos = cacheString.indexOf(",", offset);
			if(commaPos < 0) {
				int index = Integer.valueOf(cacheString.substring(offset)).intValue();
				sorted.addElement(_nodes.elementAt(index));
				break;
			}
			int index = Integer.valueOf(cacheString.substring(offset, commaPos)).intValue();
			sorted.addElement(_nodes.elementAt(index));
			offset = commaPos + 1;
		}
		_nodes = sorted;
	}
	
	//--------------------------------------------------------------------------------

	/**
	 * ハフマン木を取得する。
	 * 
	 * @return
	 */
	public HuffmanNode getTree() {
		EBLogger.log("[S]getTree", EventLogger.DEBUG_INFO);

		String cacheKey = getCacheKey();
		if(huffmanCache == null || huffmanCache.get(cacheKey) == null) {
			EBLogger.log("[-]getTree - not cached", EventLogger.DEBUG_INFO);
			doSelectionSort();
			if(huffmanCache != null) {
				huffmanCache.put(cacheKey, getCacheString());
			}
		} else {
			EBLogger.log("[-]getTree - using cached", EventLogger.DEBUG_INFO);
			sortByCacheString((String)huffmanCache.get(cacheKey));
		}
		
    	List sortedList = new SortedList(_nodes);
        while (sortedList.size() > 1) {
        	int lastIndex = sortedList.size() - 1;
        	HuffmanNode node1 = (HuffmanNode)sortedList.get(lastIndex);
        	sortedList.remove(lastIndex);
        	
        	lastIndex = sortedList.size() - 1;
        	HuffmanNode node2 = (HuffmanNode)sortedList.get(lastIndex);
        	sortedList.remove(lastIndex);
        	
        	sortedList.add(new HuffmanNode(node1, node2));
        }

		EBLogger.log("[E]getTree", EventLogger.DEBUG_INFO);
		return (HuffmanNode)sortedList.get(0);
	}
	
	/**
	 * Vectorの中でindex値が最も小さいHuffmanNodeを取得する
	 * @param list
	 * @return
	 */
	private HuffmanNode getMinimumIndexNode(Vector list) {
		HuffmanNode node = (HuffmanNode)list.elementAt(0);
		int index = 0;
		for(int i=1; i<list.size(); i++) {
			HuffmanNode loopNode = (HuffmanNode)list.elementAt(i);
			if(node.getIndex() > loopNode.getIndex()) {
				node = loopNode;
				index = i;
			}
		}
		list.removeElementAt(index);
		return node;
	}
	
	/**
	 * ノードをリストに追加する。
	 * @param node
	 */
	private void addIndex(HuffmanNode node) {
		_nodes.addElement(node);
		node.setIndex(_nodes.size() - 1);
		node.setOrgIndex(_nodes.size() - 1);
	}
	
	/**
	 * ノードのfreqを調べて、ソート済みfreqのリスト（Vector）と<freq, そのfreqを持つnodeのリスト（Vector）>を生成する。
	 * 
	 * @param node
	 */
	private void addSortedFreqs(HuffmanNode node) {
		int freq = node.getFrequency(); 
		
		Vector list = (Vector)_fresNodesMap.get(new Integer(freq));
		if(list == null) {	
			//そのfrequencyが最初に現れたとき
			list = new Vector();
			_fresNodesMap.put(new Integer(freq), list);
			
			//順番を決める
			boolean isInserted = false;
			for(int i=_sortedFreqs.size()-1; i>=0; i--) {
				Integer elem = (Integer)_sortedFreqs.elementAt(i);
				if(elem.intValue() > freq) {
					_sortedFreqs.insertElementAt(new Integer(freq), i+1);
					isInserted = true;
					break;
				}
			}
			if(!isInserted) {
				_sortedFreqs.insertElementAt(new Integer(freq), 0);
			}
		}
		list.addElement(node);
	}
	
    /**
     * 降順ソート（大->小）されたArrayList。
     * 同じ値の場合、先に入った方がindexの若い方にある。
     */
    private static class SortedList extends ArrayList {
    	public SortedList(Vector v) {
    		_vector = v;
    	}
    	
    	public boolean add(Object o) {
    		for(int i=_vector.size()-1; i>=0; i--) {
    			if( ((Comparable)_vector.elementAt(i)).compareTo(o) >= 0) {
    				_vector.insertElementAt(o, i+1);
    				return true;
    			}
    		}
    		_vector.insertElementAt(o, 0);
    		return true;
    	}
    }
}