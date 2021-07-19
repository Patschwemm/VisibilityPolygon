package geometry;

public class PolygonDrawer {

        public void draw_Labyrinth_polygon(){

            GUI.polygon.addNode(10,10);
            GUI.polygon.addNode(1250,10);
            GUI.polygon.addNode(1250,850);
            GUI.polygon.addNode(1050,850);
            GUI.polygon.addNode(1050,135);
            GUI.polygon.addNode(200,135);
            GUI.polygon.addNode(200,715);
            GUI.polygon.addNode(840,715);
            GUI.polygon.addNode(840,280);
            GUI.polygon.addNode(410,280);
            GUI.polygon.addNode(410,570);
            GUI.polygon.addNode(630,570);
            GUI.polygon.addNode(630,425);
            GUI.polygon.addNode(620,425);
            GUI.polygon.addNode(620,560);
            GUI.polygon.addNode(420,560);
            GUI.polygon.addNode(420,290);
            GUI.polygon.addNode(830,290);
            GUI.polygon.addNode(830,705);
            GUI.polygon.addNode(210,705);
            GUI.polygon.addNode(210,145);
            GUI.polygon.addNode(1040,145);
            GUI.polygon.addNode(1040,850);
            GUI.polygon.addNode(10,850);
            GUI.polygon.addNode(10,10);

        }

    public void draw_S_polygon(){

        GUI.polygon.addNode(976,299);
        GUI.polygon.addNode(1036,305);
        GUI.polygon.addNode(1138,325);
        GUI.polygon.addNode(1156,289);
        GUI.polygon.addNode(1066,181);
        GUI.polygon.addNode(896,141);
        GUI.polygon.addNode(527,120);
        GUI.polygon.addNode(300,164);
        GUI.polygon.addNode(161,278);
        GUI.polygon.addNode(163,368);
        GUI.polygon.addNode(484,429);
        GUI.polygon.addNode(807,438);
        GUI.polygon.addNode(981,474);
        GUI.polygon.addNode(941,549);
        GUI.polygon.addNode(708,582);
        GUI.polygon.addNode(364,592);
        GUI.polygon.addNode(248,635);
        GUI.polygon.addNode(553,726);
        GUI.polygon.addNode(967,710);
        GUI.polygon.addNode(1166,572);
        GUI.polygon.addNode(1164,434);
        GUI.polygon.addNode(1000,363);
        GUI.polygon.addNode(717,319);
        GUI.polygon.addNode(426,299);
        GUI.polygon.addNode(401,254);
        GUI.polygon.addNode(447,224);
        GUI.polygon.addNode(534,256);
        GUI.polygon.addNode(609,282);
        GUI.polygon.addNode(685,284);
        GUI.polygon.addNode(741,264);
        GUI.polygon.addNode(788,266);
        GUI.polygon.addNode(823,295);
        GUI.polygon.addNode(846,313);
        GUI.polygon.addNode(891,311);
        GUI.polygon.addNode(976,299);
    }

    public void draw_C_Polygon(){

        GUI.polygon.addNode(400,100);
        GUI.polygon.addNode(800,100);
        GUI.polygon.addNode(950,200);
        GUI.polygon.addNode(950,325);
        GUI.polygon.addNode(850,325);
        GUI.polygon.addNode(850,300);
        GUI.polygon.addNode(650,250);
        GUI.polygon.addNode(550,250);
        GUI.polygon.addNode(400,300);
        GUI.polygon.addNode(400,400);
        GUI.polygon.addNode(550,450);
        GUI.polygon.addNode(650,450);
        GUI.polygon.addNode(850,400);
        GUI.polygon.addNode(850,375);
        GUI.polygon.addNode(950,375);
        GUI.polygon.addNode(950,500);
        GUI.polygon.addNode(800,600);
        GUI.polygon.addNode(400,600);
        GUI.polygon.addNode(250,500);
        GUI.polygon.addNode(200,400);
        GUI.polygon.addNode(200,300);
        GUI.polygon.addNode(250,200);
        GUI.polygon.addNode(400,100);
    }

    public void draw_generic_right_cave(){
        GUI.polygon.addNode(980.0,177.0);
        GUI.polygon.addNode(678.0,131.0);
        GUI.polygon.addNode(358.0,159.0);
        GUI.polygon.addNode(173.0,333.0);
        GUI.polygon.addNode(140.0,575.0);
        GUI.polygon.addNode(280.0,736.0);
        GUI.polygon.addNode(670.0,786.0);
        GUI.polygon.addNode(998.0,767.0);
        GUI.polygon.addNode(1122.0,570.0);
        GUI.polygon.addNode(1009.0,452.0);
        GUI.polygon.addNode(762.0,446.0);
        GUI.polygon.addNode(599.0,519.0);
        GUI.polygon.addNode(479.0,562.0);
        GUI.polygon.addNode(425.0,444.0);
        GUI.polygon.addNode(516.0,340.0);
        GUI.polygon.addNode(836.0,339.0);
        GUI.polygon.addNode(1099.0,403.0);
        GUI.polygon.addNode(986.0,181.0);

//        for (Point point: GUI.polygon.getPointList()){
//            double x = point.getCenterX();
//            double dif_x = 635 - x;
//            x= 635+ dif_x;
//            System.out.println("GUI.polygon.addNode("+x+","+point.getCenterY()+")");
//
//        }
    }


    public void draw_generic_left_cave(){
        GUI.polygon.addNode(290.0,177.0);
        GUI.polygon.addNode(592.0,131.0);
        GUI.polygon.addNode(912.0,159.0);
        GUI.polygon.addNode(1097.0,333.0);
        GUI.polygon.addNode(1130.0,575.0);
        GUI.polygon.addNode(990.0,736.0);
        GUI.polygon.addNode(600.0,786.0);
        GUI.polygon.addNode(272.0,767.0);
        GUI.polygon.addNode(148.0,570.0);
        GUI.polygon.addNode(261.0,452.0);
        GUI.polygon.addNode(508.0,446.0);
        GUI.polygon.addNode(671.0,519.0);
        GUI.polygon.addNode(791.0,562.0);
        GUI.polygon.addNode(845.0,444.0);
        GUI.polygon.addNode(754.0,340.0);
        GUI.polygon.addNode(434.0,339.0);
        GUI.polygon.addNode(171.0,403.0);
        GUI.polygon.addNode(290.0,177.0);
    }


    public void draw_reverse_S_Polygon(){
        GUI.polygon.addNode(983.0,121.0);
        GUI.polygon.addNode(757.0,79.0);
        GUI.polygon.addNode(396.0,102.0);
        GUI.polygon.addNode(240.0,260.0);
        GUI.polygon.addNode(253.0,374.0);
        GUI.polygon.addNode(393.0,390.0);
        GUI.polygon.addNode(488.0,370.0);
        GUI.polygon.addNode(597.0,363.0);
        GUI.polygon.addNode(715.0,362.0);
        GUI.polygon.addNode(826.0,368.0);
        GUI.polygon.addNode(836.0,430.0);
        GUI.polygon.addNode(689.0,472.0);
        GUI.polygon.addNode(433.0,531.0);
        GUI.polygon.addNode(229.0,610.0);
        GUI.polygon.addNode(222.0,710.0);
        GUI.polygon.addNode(543.0,799.0);
        GUI.polygon.addNode(1006.0,781.0);
        GUI.polygon.addNode(1124.0,677.0);
        GUI.polygon.addNode(891.0,655.0);
        GUI.polygon.addNode(584.0,676.0);
        GUI.polygon.addNode(494.0,641.0);
        GUI.polygon.addNode(692.0,577.0);
        GUI.polygon.addNode(998.0,555.0);
        GUI.polygon.addNode(1170.0,383.0);
        GUI.polygon.addNode(1035.0,193.0);
        GUI.polygon.addNode(987.0,122.0);

    }

    public void draw_z_polygon(){
        GUI.polygon.addNode(980.0,150.0);
        GUI.polygon.addNode(681.0,145.0);
        GUI.polygon.addNode(304.0,137.0);
        GUI.polygon.addNode(295.0,281.0);
        GUI.polygon.addNode(637.0,389.0);
        GUI.polygon.addNode(536.0,290.0);
        GUI.polygon.addNode(867.0,379.0);
        GUI.polygon.addNode(542.0,479.0);
        GUI.polygon.addNode(292.0,512.0);
        GUI.polygon.addNode(294.0,686.0);
        GUI.polygon.addNode(1074.0,694.0);
        GUI.polygon.addNode(1062.0,596.0);
        GUI.polygon.addNode(496.0,580.0);
        GUI.polygon.addNode(1023.0,500.0);
        GUI.polygon.addNode(1007.0,368.0);
        GUI.polygon.addNode(981.0,152.0);

    }

    public void draw_right_corridor_caves(){
        GUI.polygon.addNode(1092.0,90.0);
        GUI.polygon.addNode(272.0,78.0);
        GUI.polygon.addNode(263.0,756.0);
        GUI.polygon.addNode(1209.0,761.0);
        GUI.polygon.addNode(1175.0,619.0);
        GUI.polygon.addNode(451.0,636.0);
        GUI.polygon.addNode(452.0,538.0);
        GUI.polygon.addNode(1186.0,526.0);
        GUI.polygon.addNode(1165.0,430.0);
        GUI.polygon.addNode(586.0,454.0);
        GUI.polygon.addNode(583.0,389.0);
        GUI.polygon.addNode(1144.0,368.0);
        GUI.polygon.addNode(1133.0,316.0);
        GUI.polygon.addNode(680.0,317.0);
        GUI.polygon.addNode(688.0,275.0);
        GUI.polygon.addNode(1123.0,259.0);
        GUI.polygon.addNode(1126.0,222.0);
        GUI.polygon.addNode(810.0,221.0);
        GUI.polygon.addNode(818.0,178.0);
        GUI.polygon.addNode(1126.0,182.0);
        GUI.polygon.addNode(1130.0,140.0);
        GUI.polygon.addNode(1130.0,89.0);
        GUI.polygon.addNode(1097.0,91.0);
    }

    public void draw_check_rec_area_tester(){
        GUI.polygon.addNode(141.0,517.0);
        GUI.polygon.addNode(137.0,758.0);
        GUI.polygon.addNode(597.0,752.0);
        GUI.polygon.addNode(446.0,417.0);
        GUI.polygon.addNode(639.0,288.0);
        GUI.polygon.addNode(904.0,393.0);
        GUI.polygon.addNode(861.0,563.0);
        GUI.polygon.addNode(679.0,569.0);
        GUI.polygon.addNode(644.0,842.0);
        GUI.polygon.addNode(1251.0,837.0);
        GUI.polygon.addNode(1244.0,19.0);
        GUI.polygon.addNode(49.0,13.0);
        GUI.polygon.addNode(58.0,127.0);
        GUI.polygon.addNode(542.0,119.0);
        GUI.polygon.addNode(976.0,155.0);
        GUI.polygon.addNode(1118.0,345.0);
        GUI.polygon.addNode(1124.0,707.0);
        GUI.polygon.addNode(730.0,722.0);
        GUI.polygon.addNode(1066.0,605.0);
        GUI.polygon.addNode(1016.0,312.0);
        GUI.polygon.addNode(697.0,185.0);
        GUI.polygon.addNode(220.0,169.0);
        GUI.polygon.addNode(132.0,511.0);
    }

    public void draw_custom_polygon(){


//        GUI.polygon.addNode(402.0,643.0);
//        GUI.polygon.addNode(628.0,633.0);
//        GUI.polygon.addNode(501.0,412.0);
//        GUI.polygon.addNode(652.0,199.0);
//        GUI.polygon.addNode(500.0,44.0);
//        GUI.polygon.addNode(582.0,7.0);
//        GUI.polygon.addNode(941.0,16.0);
//        GUI.polygon.addNode(595.0,57.0);
//        GUI.polygon.addNode(737.0,199.0);
//        GUI.polygon.addNode(615.0,417.0);
//        GUI.polygon.addNode(774.0,636.0);
//        GUI.polygon.addNode(762.0,735.0);
//        GUI.polygon.addNode(471.0,737.0);
//        GUI.polygon.addNode(401.0,640.0);

//        GUI.polygon.addNode(449.0,136.0);
//        GUI.polygon.addNode(872.0,184.0);
//        GUI.polygon.addNode(388.0,366.0);
//        GUI.polygon.addNode(832.0,403.0);
//        GUI.polygon.addNode(251.0,662.0);
//        GUI.polygon.addNode(866.0,657.0);
//        GUI.polygon.addNode(300.0,797.0);
//        GUI.polygon.addNode(1179.0,779.0);
//        GUI.polygon.addNode(1201.0,710.0);
//        GUI.polygon.addNode(792.0,730.0);
//        GUI.polygon.addNode(1027.0,578.0);
//        GUI.polygon.addNode(732.0,550.0);
//        GUI.polygon.addNode(1160.0,374.0);
//        GUI.polygon.addNode(759.0,329.0);
//        GUI.polygon.addNode(1140.0,169.0);
//        GUI.polygon.addNode(607.0,59.0);
//        GUI.polygon.addNode(450.0,139.0);


        GUI.polygon.addNode(814.0,132.0);
        GUI.polygon.addNode(555.0,127.0);
        GUI.polygon.addNode(422.0,268.0);
        GUI.polygon.addNode(618.0,377.0);
        GUI.polygon.addNode(383.0,528.0);
        GUI.polygon.addNode(627.0,604.0);
        GUI.polygon.addNode(364.0,748.0);
        GUI.polygon.addNode(841.0,761.0);
        GUI.polygon.addNode(595.0,696.0);
        GUI.polygon.addNode(830.0,589.0);
        GUI.polygon.addNode(585.0,474.0);
        GUI.polygon.addNode(918.0,407.0);
        GUI.polygon.addNode(580.0,254.0);
        GUI.polygon.addNode(632.0,183.0);
        GUI.polygon.addNode(815.0,134.0);


    }

    public void draw_custom_polygon2() {
        GUI.polygon.addNode(795.0,136.0);
        GUI.polygon.addNode(554.0,133.0);
        GUI.polygon.addNode(363.0,144.0);
        GUI.polygon.addNode(229.0,310.0);
        GUI.polygon.addNode(180.0,542.0);
        GUI.polygon.addNode(157.0,698.0);
        GUI.polygon.addNode(287.0,468.0);
        GUI.polygon.addNode(372.0,301.0);
        GUI.polygon.addNode(601.0,222.0);
        GUI.polygon.addNode(715.0,246.0);
        GUI.polygon.addNode(641.0,444.0);
        GUI.polygon.addNode(576.0,627.0);
        GUI.polygon.addNode(561.0,772.0);
        GUI.polygon.addNode(793.0,814.0);
        GUI.polygon.addNode(1120.0,821.0);
        GUI.polygon.addNode(1232.0,805.0);
        GUI.polygon.addNode(1144.0,767.0);
        GUI.polygon.addNode(919.0,748.0);
        GUI.polygon.addNode(768.0,709.0);
        GUI.polygon.addNode(755.0,577.0);
        GUI.polygon.addNode(812.0,318.0);
        GUI.polygon.addNode(867.0,243.0);
        GUI.polygon.addNode(977.0,202.0);
        GUI.polygon.addNode(1020.0,347.0);
        GUI.polygon.addNode(967.0,474.0);
        GUI.polygon.addNode(892.0,586.0);
        GUI.polygon.addNode(888.0,690.0);
        GUI.polygon.addNode(1173.0,722.0);
        GUI.polygon.addNode(1074.0,678.0);
        GUI.polygon.addNode(980.0,616.0);
        GUI.polygon.addNode(1053.0,533.0);
        GUI.polygon.addNode(1181.0,545.0);
        GUI.polygon.addNode(1116.0,624.0);
        GUI.polygon.addNode(1204.0,605.0);
        GUI.polygon.addNode(1241.0,447.0);
        GUI.polygon.addNode(1125.0,243.0);
        GUI.polygon.addNode(980.0,143.0);
        GUI.polygon.addNode(789.0,138.0);
    }
}
