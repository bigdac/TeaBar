package teabar.ph.com.teabar.util;

public class Utils{
        /**
         * 得到某一天的星期几

         * @param week
         * @return
         */
        public static int getWeek(int week){
            int mWeek=0;
            switch (week) {
                case (1):
                    mWeek=7;
                    break;
                case (2):
                    mWeek=1;
                    break;
                case 3:
                    mWeek=2;
                    break;
                case 4:
                    mWeek=3;
                    break;
                case 5:
                    mWeek=4;
                    break;
                case 6:
                    mWeek=5;
                    break;
                case 7:
                    mWeek=6;
                    break;
            }
            return mWeek;
        }
}
