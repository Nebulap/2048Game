package com.example.game2048;

import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;


import java.io.*;
import java.net.URL;
import java.util.*;

public class MainController implements Initializable {
    @FXML private Pane bestPane;
    @FXML private Pane scorePane;
    @FXML private StackPane background;
    @FXML private GridPane gz;
    @FXML private StackPane currentScoreStackPane;
    @FXML private StackPane bestScoreStackPane;
    @FXML private Pane newGame;
    @FXML private Pane mainPane;
    @FXML void newGame(MouseEvent event){
        gz.getChildren().clear();
        tileGenerator = new TileGenerator();
        createLatticePane();
        updateAllLatticePaneColorAndLabel();
        //添加两个随机位置随机数值的方块
        createRandomNumbersOnRandomLocation();
        createRandomNumbersOnRandomLocation();
        currentScore = 0;
        addScore(0);

        try {
            gridPaneStackPane.getChildren().remove(0);
        }catch (Exception e){
            System.out.println("重新开始");
        }

    }

    private static StackPane gridPaneStackPane;

    private static Text currentScoreText = new Text("0"){{
        setStyle("-fx-fill: rgb(255,255,255);");
        setFont(Font.font("楷体", FontWeight.BOLD,26));
    }};;

    private static Text bestScoreText = new Text("0"){{
        setStyle("-fx-fill: rgb(255,255,255);");
        setFont(Font.font("楷体", FontWeight.BOLD,26));
    }};;;



    private static int currentScore;


    private static Map<Integer,List<Integer>> idToCoordinateMap = new HashMap<>();
    private static Map<Integer,Integer> idToNumberMap = new HashMap<>();
    private static Map<Integer,List<Node>> idToGridPaneNodesListMap = new HashMap<>();
    private static int[][] grid = new int[4][4];

    private static TileGenerator tileGenerator;


    private static List<int[]> randomAndWeightList = new ArrayList<>();
    //初始化级别映射数值及权重
    private static Map<Integer,List<int[]>> gradeToRandomValuesAndWeight = Map.of(
            1,List.of(new int[]{2,4},new int[]{80,20}),
            2,List.of(new int[]{4,8},new int[]{90,10}),
            3,List.of(new int[]{8,16},new int[]{80,20})
    );
    private static int grade = 1;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //设置控件背景颜色和圆角
        background.setBackground(new Background(new BackgroundFill(Color.rgb(187,173,160),new CornerRadii(15),null)));
        scorePane.setBackground(new Background(new BackgroundFill(Color.rgb(187,173,160),new CornerRadii(15),null)));
        bestPane.setBackground(new Background(new BackgroundFill(Color.rgb(187,173,160),new CornerRadii(15),null)));
        newGame.setBackground(new Background(new BackgroundFill(Color.rgb(143,122,102),new CornerRadii(15),null)));


        currentScoreStackPane.getChildren().add(currentScoreText);
        bestScoreStackPane.getChildren().add(bestScoreText);

        //new TileGenerator
         tileGenerator = new TileGenerator();
        createLatticePane();
        updateAllLatticePaneColorAndLabel();
        //添加两个随机位置随机数值的方块
        createRandomNumbersOnRandomLocation();
        createRandomNumbersOnRandomLocation();

        //创建游戏结束面板
        gridPaneStackPane = new StackPane(){{
            setPrefHeight(749);
            setPrefWidth(746);
            setLayoutX(31);
            setLayoutY(187);
            mainPane.getChildren().add(this);
        }};
    }

    /**
     * 游戏结束界面
     */
    private static void gameOver(){
        gridPaneStackPane.getChildren().add(new StackPane(){{
            setStyle("-fx-background-color: rgba(250,248,239,0.5);");
            getChildren().add(new Text("Game Over!"){{
                setFont(Font.font("楷体",FontWeight.BOLD,80));
                setFill(Color.rgb(119,110,101));
            }});
            getChildren().add(new Text("2900221581制作"){{
                setTranslateY(100);
                setFont(Font.font("楷体",FontWeight.BOLD,80));
                setFill(Color.rgb(119,110,101));
            }});
        }});
    }

    /**
     * 数字和对应的颜色
     */
    private static class Colors {
        private static final Color 零 = Color.rgb(205,193,180);
        private static final Color 二 = Color.rgb(238, 228, 218);
        private static final Color 四 = Color.rgb(237, 224, 200);
        private static final Color 八 = Color.rgb(242, 177, 121);
        private static final Color 十六 = Color.rgb(245, 149, 99);
        private static final Color 三十二 = Color.rgb(246,124,95);
        private static final Color 六十四 = Color.rgb(246,94,59);
        private static final Color 一百二十八 = Color.rgb(237,207,114);
        private static final Color 两百五十六 = Color.rgb(237,204,97);
        private static final Color 五百一十二 = Color.rgb(197,236,81);
        private static final Color 一千零二十四 = Color.rgb(56,210,59);
        private static final Color 二千四百四十八 = Color.rgb(42,203,173);
        private static final Color 四千九百九十六 = Color.rgb(35,108,192);



        public static final Map<Integer,Color> labelColorMap = new HashMap<>(){{
            put(0,Color.rgb(205,193,180));
            put(2,Color.rgb(119,110,101));
            put(4,Color.rgb(119,110,101));
            put(8,Color.rgb(249,246,242));
            put(16,Color.rgb(249,246,242));
            put(32,Color.rgb(249,246,242));
            put(64,Color.rgb(249,246,242));
            put(128,Color.rgb(249,246,242));
            put(256,Color.rgb(249,246,242));
            put(512,Color.rgb(249,246,242));
            put(1024,Color.rgb(249,246,242));
            put(2048,Color.rgb(249,246,242));
            put(4096,Color.rgb(249,246,242));
        }};

        public static final Map<Integer, Color> digitToBackgroungColorMap = createDigitToColorMap();

        private static Map<Integer, Color> createDigitToColorMap() {
            Map<Integer, Color> map = new HashMap<>();
            map.put(0,零);
            map.put(2, 二);
            map.put(4, 四);
            map.put(8, 八);
            map.put(16, 十六);
            map.put(32, 三十二);
            map.put(64, 六十四);
            map.put(128, 一百二十八);
            map.put(256, 两百五十六);
            map.put(512, 五百一十二);
            map.put(1024, 一千零二十四);
            map.put(2048, 二千四百四十八);
            map.put(4096, 四千九百九十六);
            return map;
        }


    }

    /**
     * 为格子面板添加子面板
     * @return void
     * @Time 2023-06-09 23:20
     */
    private void createLatticePane() {
        int row = 0;
        int column = 0;
        int id = 1;

        while (column < 4) {
            row = 0; // 重置行数为0
            while (row < 4) {
                /*背景颜色*/
                StackPane latticePane = new StackPane();
                latticePane.setId(String.valueOf(id));//设置id
                gz.add(latticePane,row,column);

                /*标签*/
                Label numberLabel = new Label("0");
                numberLabel.setStyle("-fx-font-size:50;-fx-font-weight:bold;");
                latticePane.getChildren().add(numberLabel);

                idToGridPaneNodesListMap.put(id,List.of(latticePane,numberLabel));//id映射背景和标签列表

                idToCoordinateMap.put(id,List.of(column,row));//存储坐标
                idToNumberMap.put(id,0);
                grid[column][row] = 0;
                id++;
                row++;
            }
            column++;
        }
        addScore(0);
    }

    /**
     * 更新所有子面板的颜色和标签
     * @return void
     * @Time 2023-06-10 00:00
     */

    private static void updateAllLatticePaneColorAndLabel(){
        for (int id : idToGridPaneNodesListMap.keySet()){
            /*读取控件*/
            StackPane latticePane = (StackPane) idToGridPaneNodesListMap.get(id).get(0);
            Label numberLabel = (Label) idToGridPaneNodesListMap.get(id).get(1);

            /*读取数字*/
            int number = idToNumberMap.get(id);

//            latticePane.setBackground(Background.fill(Colors.digitToBackgroungColorMap.get(number)));//设置背景颜色
            latticePane.setBackground(new Background(new BackgroundFill(Colors.digitToBackgroungColorMap.get(number),new CornerRadii(5),null)));//设置背景颜色
            numberLabel.setTextFill(Colors.labelColorMap.get(number));//设置标签颜色
            numberLabel.setText(String.valueOf(number));//设置标签文本
        }
    }

    /**
     * 监听上下左右按键
     * @Time 2023-06-10 00:15
     */
    public static class KeyboardListener implements EventHandler<KeyEvent> {
        @Override
        public void handle(KeyEvent event) {
            if (event.getCode() == KeyCode.W) {
                // 处理向上箭头键的按下事件
                updateNumber("W");
            } else if (event.getCode() == KeyCode.S) {
                // 处理向下箭头键的按下事件
                updateNumber("S");
            } else if (event.getCode() == KeyCode.A) {
                updateNumber("A");
                // 处理向左箭头键的按下事件
            } else if (event.getCode() == KeyCode.D) {
                // 处理向右箭头键的按下事件
                updateNumber("D");
            }
        }
    }

    /**
     *根据按下的键更新数字
     * @param key 按键
     */
    private static void updateNumber(String key){


        switch (key){
            case "A"->{
                moveLatticeAndMerge.moveLeft.moveLeft();
                updateAllLatticePaneColorAndLabel();
            }

            case "D" -> {
                moveLatticeAndMerge.moveRight.moveRight();
                updateAllLatticePaneColorAndLabel();
            }

            case "W" -> {
                moveLatticeAndMerge.moveUp.moveUp();
                updateAllLatticePaneColorAndLabel();
            }

            case "S" -> {
                moveLatticeAndMerge.moveDown.moveDown();
                updateAllLatticePaneColorAndLabel();
            }
        }

    }

    /**
     * 移动并判断是否合并方块类
     */
    private static class moveLatticeAndMerge{
        // 左移动
        public class moveLeft{
            private static boolean canMoveLeft() {
                for (int row = 0; row < 4; row++) {
                    for (int col = 1; col < 4; col++) {
                        if ((grid[row][col] != 0 && (grid[row][col - 1] == 0) || (grid[row][col - 1] == grid[row][col]))) {
                            return true;
                        }
                    }
                }
                return false;
            }

            public static void moveLeft() {
                if (canMoveLeft()) {
                    boolean[][] merged = new boolean[4][4]; // 标记已经发生过合并的方块

                    for (int row = 0; row < 4; row++) {
                        for (int col = 1; col < 4; col++) {
                            if (grid[row][col] != 0) {
                                mergeLeft(row, col, merged);
                            }
                        }
                    }
                    createRandomNumbersOnRandomLocation();
                    updateAllLatticePaneColorAndLabel();
                    //判断游戏是否结束
                    checkGameOver();
                }
            }

            private static void mergeLeft(int row, int col, boolean[][] merged) {
                int currentValue = grid[row][col];
                int targetCol = col - 1;

                while (targetCol >= 0) {
                    if (grid[row][targetCol] == 0) {
                        // 当前方块左边的方块为空，移动到该位置
                        grid[row][targetCol] = currentValue;
                        grid[row][targetCol + 1] = 0;

                        int newID = MapUtils.findKeysByValue(idToCoordinateMap, List.of(row, targetCol)).get(0);
                        int oldID = MapUtils.findKeysByValue(idToCoordinateMap, List.of(row, targetCol + 1)).get(0);
                        idToNumberMap.put(newID, currentValue);
                        idToNumberMap.put(oldID, 0);
                        col--;
                        targetCol--;
                    } else if (grid[row][targetCol] == currentValue && !merged[row][targetCol]) {
                        // 当前方块左边的方块与其相等，并且未发生过合并
                        int newValue = grid[row][targetCol] * 2;
                        grid[row][targetCol] = newValue;
                        grid[row][targetCol + 1] = 0;

                        int newID = MapUtils.findKeysByValue(idToCoordinateMap, List.of(row, targetCol)).get(0);
                        int oldID = MapUtils.findKeysByValue(idToCoordinateMap, List.of(row, targetCol + 1)).get(0);
                        idToNumberMap.put(newID, newValue);
                        new TileMergeAnimation(newID);
                        idToNumberMap.put(oldID, 0);
                        // 更新分数等相关逻辑
                        addScore(newValue);

                        merged[row][targetCol] = true; // 标记已经发生过合并
                        break;
                    } else {
                        // 当前方块左边的方块与其不相等，无法继续合并
                        break;
                    }
                }
            }

        }
        // 右移动
        public class moveRight {
            private static boolean canMoveRight() {
                for (int row = 0; row < 4; row++) {
                    for (int col = 2; col >= 0; col--) {
                        if ((grid[row][col] != 0 && (grid[row][col + 1] == 0 || grid[row][col + 1] == grid[row][col]))) {
                            return true;
                        }
                    }
                }
                return false;
            }

            public static void moveRight() {
                if (canMoveRight()) {
                    boolean[][] merged = new boolean[4][4]; // 标记已经发生过合并的方块

                    for (int row = 0; row < 4; row++) {
                        for (int col = 2; col >= 0; col--) {
                            if (grid[row][col] != 0) {
                                mergeRight(row, col, merged);
                            }
                        }
                    }
                    createRandomNumbersOnRandomLocation();
                    updateAllLatticePaneColorAndLabel();
                    //判断游戏是否结束
                    checkGameOver();
                }
            }

            private static void mergeRight(int row, int col, boolean[][] merged) {
                int currentValue = grid[row][col];
                int targetCol = col + 1;

                while (targetCol <= 3) {
                    if (grid[row][targetCol] == 0) {
                        // 当前方块右边的方块为空，移动到该位置
                        grid[row][targetCol] = currentValue;
                        grid[row][targetCol - 1] = 0;

                        int newID = MapUtils.findKeysByValue(idToCoordinateMap, List.of(row, targetCol)).get(0);
                        int oldID = MapUtils.findKeysByValue(idToCoordinateMap, List.of(row, targetCol - 1)).get(0);
                        idToNumberMap.put(newID, currentValue);
                        idToNumberMap.put(oldID, 0);
                        col++;
                        targetCol++;
                    } else if (grid[row][targetCol] == currentValue && !merged[row][targetCol]) {
                        // 当前方块右边的方块与其相等，并且未发生过合并
                        int newValue = grid[row][targetCol] * 2;
                        grid[row][targetCol] = newValue;
                        grid[row][targetCol - 1] = 0;

                        int newID = MapUtils.findKeysByValue(idToCoordinateMap, List.of(row, targetCol)).get(0);
                        int oldID = MapUtils.findKeysByValue(idToCoordinateMap, List.of(row, targetCol - 1)).get(0);
                        idToNumberMap.put(newID, newValue);
                        new TileMergeAnimation(newID);
                        idToNumberMap.put(oldID, 0);
                        // 更新分数等相关逻辑
                        addScore(newValue);

                        merged[row][targetCol] = true; // 标记已经发生过合并
                        break;
                    } else {
                        // 当前方块右边的方块与其不相等，无法继续合并
                        break;
                    }
                }
            }

        }
        // 上移动
        public class moveUp {
            private static boolean canMoveUp() {
                for (int col = 0; col < 4; col++) {
                    for (int row = 1; row < 4; row++) {
                        if ((grid[row][col] != 0 && (grid[row - 1][col] == 0 || grid[row - 1][col] == grid[row][col]))) {
                            return true;
                        }
                    }
                }
                return false;
            }

            public static void moveUp() {
                if (canMoveUp()) {
                    boolean[][] merged = new boolean[4][4]; // 标记已经发生过合并的方块

                    for (int col = 0; col < 4; col++) {
                        for (int row = 1; row < 4; row++) {
                            if (grid[row][col] != 0) {
                                mergeUp(row, col, merged);
                            }
                        }
                    }
                    createRandomNumbersOnRandomLocation();
                    updateAllLatticePaneColorAndLabel();
                    //判断游戏是否结束
                    checkGameOver();
                }
            }

            private static void mergeUp(int row, int col, boolean[][] merged) {
                int currentValue = grid[row][col];
                int targetRow = row - 1;

                while (targetRow >= 0) {
                    if (grid[targetRow][col] == 0) {
                        // 当前方块上方的方块为空，移动到该位置
                        grid[targetRow][col] = currentValue;
                        grid[targetRow + 1][col] = 0;

                        int newID = MapUtils.findKeysByValue(idToCoordinateMap, List.of(targetRow, col)).get(0);
                        int oldID = MapUtils.findKeysByValue(idToCoordinateMap, List.of(targetRow + 1, col)).get(0);
                        idToNumberMap.put(newID, currentValue);
                        idToNumberMap.put(oldID, 0);
                        row--;
                        targetRow--;
                    } else if (grid[targetRow][col] == currentValue && !merged[targetRow][col]) {
                        // 当前方块上方的方块与其相等，并且未发生过合并
                        int newValue = grid[targetRow][col] * 2;
                        grid[targetRow][col] = newValue;
                        grid[targetRow + 1][col] = 0;

                        int newID = MapUtils.findKeysByValue(idToCoordinateMap, List.of(targetRow, col)).get(0);
                        int oldID = MapUtils.findKeysByValue(idToCoordinateMap, List.of(targetRow + 1, col)).get(0);
                        idToNumberMap.put(newID, newValue);
                        new TileMergeAnimation(newID);
                        idToNumberMap.put(oldID, 0);
                        // 更新分数等相关逻辑
                        addScore(newValue);

                        merged[targetRow][col] = true; // 标记已经发生过合并
                        break;
                    } else {
                        // 当前方块上方的方块与其不相等，无法继续合并
                        break;
                    }
                }
            }

        }
        // 下移动
        public class moveDown {
            private static boolean canMoveDown() {
                for (int col = 0; col < 4; col++) {
                    for (int row = 2; row >= 0; row--) {
                        if ((grid[row][col] != 0 && (grid[row + 1][col] == 0 || grid[row + 1][col] == grid[row][col]))) {
                            return true;
                        }
                    }
                }
                return false;
            }

            public static void moveDown() {
                if (canMoveDown()) {
                    boolean[][] merged = new boolean[4][4]; // 标记已经发生过合并的方块

                    for (int col = 0; col < 4; col++) {
                        for (int row = 2; row >= 0; row--) {
                            if (grid[row][col] != 0) {
                                mergeDown(row, col, merged);
                            }
                        }
                    }
                    createRandomNumbersOnRandomLocation();
                    updateAllLatticePaneColorAndLabel();
                    //判断游戏是否结束
                    checkGameOver();
                }
            }

            private static void mergeDown(int row, int col, boolean[][] merged) {
                int currentValue = grid[row][col];
                int targetRow = row + 1;

                while (targetRow <= 3) {
                    if (grid[targetRow][col] == 0) {
                        // 当前方块下方的方块为空，移动到该位置
                        grid[targetRow][col] = currentValue;
                        grid[targetRow - 1][col] = 0;

                        int newID = MapUtils.findKeysByValue(idToCoordinateMap, List.of(targetRow, col)).get(0);
                        int oldID = MapUtils.findKeysByValue(idToCoordinateMap, List.of(targetRow - 1, col)).get(0);
                        idToNumberMap.put(newID, currentValue);
                        idToNumberMap.put(oldID, 0);
                        row++;
                        targetRow++;
                    } else if (grid[targetRow][col] == currentValue && !merged[targetRow][col]) {
                        // 当前方块下方的方块与其相等，并且未发生过合并
                        int newValue = grid[targetRow][col] * 2;
                        grid[targetRow][col] = newValue;
                        grid[targetRow - 1][col] = 0;

                        int newID = MapUtils.findKeysByValue(idToCoordinateMap, List.of(targetRow, col)).get(0);
                        int oldID = MapUtils.findKeysByValue(idToCoordinateMap, List.of(targetRow - 1, col)).get(0);
                        idToNumberMap.put(newID, newValue);
                        new TileMergeAnimation(newID);
                        idToNumberMap.put(oldID, 0);
                        // 更新分数等相关逻辑
                        addScore(newValue);

                        merged[targetRow][col] = true; // 标记已经发生过合并
                        break;
                    } else {
                        // 当前方块下方的方块与其不相等，无法继续合并
                        break;
                    }
                }
            }

        }

    }

    /**
     * 随机生成一个数字在随机位置
     * @return void
     */
    private static void createRandomNumbersOnRandomLocation(){
        //创建闲置面板列表
        List<Integer> freeLocation = new ArrayList<>();

        //添加闲置面板
        for (int id : idToNumberMap.keySet()){
            if (idToNumberMap.get(id)==0){
                freeLocation.add(id);
            }
        }

        // 生成随机数
        Random random = new Random();
        int randomIndex = random.nextInt(freeLocation.size());

        //根据等级更新随机生成数的值和权重
        updateRandomAndWeight();
        // 获取随机值
        int randomValue = freeLocation.get(randomIndex);//随机位置
        int randomNumber = tileGenerator.generateNewTileValue();//随机数值

        //创建动画
        new TileGenerateAnimation(randomValue);

        int row = idToCoordinateMap.get(randomValue).get(0);
        int col = idToCoordinateMap.get(randomValue).get(1);

        idToNumberMap.put(randomValue,randomNumber);
        grid[row][col] = randomNumber;

        updateOneLatticePane(randomValue);


    }

    /**
     * 设置单个面板的标签,标签颜色和背景颜色
     * @param id
     */
    private static void updateOneLatticePane(int id){
        /*读取控件*/
        StackPane latticePane = (StackPane) idToGridPaneNodesListMap.get(id).get(0);
        Label numberLabel = (Label) idToGridPaneNodesListMap.get(id).get(1);

        /*读取数字*/
        int number = idToNumberMap.get(id);

        latticePane.setBackground(new Background(new BackgroundFill(Colors.digitToBackgroungColorMap.get(number),new CornerRadii(5),null)));//设置背景颜色
        numberLabel.setTextFill(Colors.labelColorMap.get(number));//设置标签颜色
        numberLabel.setText(String.valueOf(number));//设置标签文本
    }


    private static class MapUtils{
        /**
         * 通过values查找键
         * @param map
         * @param value
         * @return
         */
        private static List<Integer> findKeysByValue(Map<Integer, List<Integer>> map, List<Integer> value) {
            List<Integer> keys = new ArrayList<>();

            for (Map.Entry<Integer, List<Integer>> entry : map.entrySet()) {
                if (entry.getValue().equals(value)) {
                    keys.add(entry.getKey());
                }
            }

            return keys;
        }
    }

    /**
     * 随机生成方块
     */
    private class TileGenerator {
        private Random random;

        public TileGenerator() {
            random = new Random();
        }

        public int generateNewTileValue() {
            int totalWeight = 0;
            for (int weight : gradeToRandomValuesAndWeight.get(grade).get(1)) {
                totalWeight += weight;
            }

            int randomValue = random.nextInt(totalWeight) + 1; // 生成一个介于 1 和总权重之间的随机值

            int cumulativeWeight = 0;
            for (int i = 0; i < gradeToRandomValuesAndWeight.get(grade).get(0).length; i++) {
                cumulativeWeight += gradeToRandomValuesAndWeight.get(grade).get(0)[i];
                if (randomValue <= cumulativeWeight) {
                    return gradeToRandomValuesAndWeight.get(grade).get(0)[i];
                }
            }

            return gradeToRandomValuesAndWeight.get(grade).get(0)[0];
        }
    }

    /**
     * 更新随机数值及其权重列表
     */
    private static void updateRandomAndWeight(){
        int maxValues = 0;
        for (int id : idToNumberMap.keySet()){
            if (idToNumberMap.get(id)>maxValues){
                maxValues = idToNumberMap.get(id);
            }
        }
        boolean lastLvValues = true;
        if (maxValues<=512){
            grade = 1;
        }else if (maxValues<=1024){
            for (int id : idToNumberMap.keySet()){
                if (idToNumberMap.get(id)==2){
                    lastLvValues = false;
                }
            }
            if (lastLvValues){
                grade = 2;
            }
        }else if (maxValues<=2048){
            for (int id : idToNumberMap.keySet()){
                if (idToNumberMap.get(id)==4){
                    lastLvValues = false;
                }
            }
            if (lastLvValues){
                grade = 3;
            }
        }
    }

    /**
     * 方块动画效果
     */
    public static class TileGenerateAnimation {
        private static final int TILE_SIZE = 150;
        private static final Duration ANIMATION_DURATION = Duration.millis(250);

        private StackPane root;

        public TileGenerateAnimation(int id) {
            this.root = (StackPane) idToGridPaneNodesListMap.get(id).get(0);
            animateNewTile();
        }


        public void animateNewTile() {
            root.setScaleX(0);
            root.setScaleY(0);

            ScaleTransition scaleTransition = new ScaleTransition(ANIMATION_DURATION, root);
            scaleTransition.setToX(1);
            scaleTransition.setToY(1);
            scaleTransition.play();
        }

    }

    /**
     * 方块合并动画
     */
    public static class TileMergeAnimation {
        private static final int TILE_SIZE = 150;
        private static final Duration ANIMATION_DURATION = Duration.millis(125);

        private StackPane root;

        public TileMergeAnimation(int id) {
            this.root = (StackPane) idToGridPaneNodesListMap.get(id).get(0);
            animateNewTile();
        }

        public void animateNewTile() {

            ScaleTransition scaleUpTransition = new ScaleTransition(ANIMATION_DURATION, root);
            scaleUpTransition.setToX(1.1);
            scaleUpTransition.setToY(1.1);

            ScaleTransition scaleDownTransition = new ScaleTransition(ANIMATION_DURATION, root);
            scaleDownTransition.setToX(1);
            scaleDownTransition.setToY(1);

            SequentialTransition sequentialTransition = new SequentialTransition(scaleUpTransition, scaleDownTransition);
            sequentialTransition.play();
        }
    }

    private static void addScore(int scors){
        currentScore = currentScore + scors;// 计算当前分数
        int highScore = HighScoreManager.readHighScore(); // 读取历史最高分数

        if (currentScore > highScore) {
            HighScoreManager.updateHighScore(currentScore); // 更新最高分数
            setBestScoreText(currentScore);
        }else {
            setBestScoreText(highScore);
        }
        setCurrentScoreText();
    }

    private static void setCurrentScoreText(){
        currentScoreText.setText(String.valueOf(currentScore));
    }
    private static void setBestScoreText(int bsetScore){
        bestScoreText.setText(String.valueOf(bsetScore));
    }



    /**
     * 历史最高分数类
     */
    public class HighScoreManager {
        private static final String FILE_PATH = "highscore.txt";

        /**
         * 读取最高分数
         * @return
         */
        public static int readHighScore() {
            try {
                File file = new File(FILE_PATH);
                if (file.exists()) {
                    Scanner scanner = new Scanner(file);
                    int highScore = scanner.nextInt();
                    scanner.close();
                    return highScore;
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return 0; // 默认返回0表示没有历史最高分数
        }

        /**
         * 更新最高分数
         * @param score
         */
        public static void updateHighScore(int score) {
            try {
                PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH));
                writer.print(score);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 检查游戏是否结束
     */
    private static void checkGameOver() {
        boolean gameOver = true;

        // 检查是否存在可移动的方块
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                if (grid[row][col] == 0 ||
                        (col < 3 && grid[row][col] == grid[row][col + 1]) ||
                        (row < 3 && grid[row][col] == grid[row + 1][col])) {
                    gameOver = false;
                    break;
                }
            }
        }

        if (gameOver) {
            gameOver();
        }
    }


}
