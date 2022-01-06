package com.example.blockbreak;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.Random;

public class MyView extends View {
    int xstep; // 공의 x좌표
    int ystep; // 공의 y좌표
    int xMov; // 공의 X 축 증감
    int yMov; // 공의 Y 축 증감

    int angle; // 공의 이동각도
    int size; // 공의 크기

    Rect rect; // 공 사각 영역
    Rect barRect; // 바 사각 영역
    int xBar; // 바의 x위치
    int yBar; // 바의 y 위치
    int barWidth = 500;// 바의 너비
    int barHeight = 40; // 바의 높이
    int blockXpos; // 블록의 시작위치
    int blockYpos;
    int blockSize; // 블록의 크기

    Block[] blocks;

    public MyView(Context context) {
        super(context); // 화면안의 랜덤한 위치에 생성
        xstep =500;
        ystep =1800; // 임의의 위치에 생성
        angle = 90; //90 수직 하강- 270 수직 상승
        // 생성된 각도로 x 증감, y 증감 계
        Movement mv = new Movement(angle);
        xMov= mv.xMov;
        yMov=mv.yMov;

        size = 60; // 네모크기 4로 설정

        xBar = 100;
        yBar = 2000;

        barRect = new Rect();//바

        barRect.left = xBar;
        barRect.top = yBar;
        barRect.right = barRect.left + barWidth;
        barRect.bottom = barRect.top + barHeight; // 바의 사각영역 설정

        rect = new Rect(); //공

        rect.left = xstep; // 사각 영역 설정
        rect.top = ystep;
        rect.right = xstep + size;
        rect.bottom = xstep + size;

        blockXpos = 100; // 블록의 시작 위치
        blockYpos = 100;
        blockSize = 35; // 블록의 크기

        // 블록 배열객체 생성 초기화
        blocks = new Block[4]; //블록 여러개
        for (int i = 0; i < blocks.length / 2; i++) {
            blocks[i] = new Block(blockXpos + i * (blockSize + 2), blockYpos, blockSize, blockSize, true);
        } //첫번째 줄 블록 <블록 간 간격 2>
        for (int i = 0; i < blocks.length / 2; i++) {
            blocks[i + 2] = new Block(blockXpos + i * (blockSize + 2), blockYpos + blockSize + 2, blockSize, blockSize, true);
        }//두번째 줄 블록 < 위아래 첫번째와 블록 간격 2>
    }

    // 선택한 범위의 각도가 생성된다.
    private void makeAngle(int start, int range) {
        angle = new Random().nextInt(range) + start;
    }

    //★안드로이드 코드 내부에 있는 거 계속 갱신★
    public void onDraw(Canvas canvas) {
        Paint pnt = new Paint(); // 페인트 객체 생성
        pnt.setColor(Color.BLUE); // 파란색 색깔 선택
        canvas.drawColor(Color.WHITE); // 하얀색 배경

        // 왼쪽 벽에 부딧친 경우(오른쪽 일수도 있음)
        if (xstep < 0) {
            //위로 올라가면서 부딪침
            Log.d("left", angle+"");
            makeAngle(270, 180);
            xstep = 0;
        }
        // 윗쪽벽에 부딧친 경우
        if (ystep < 0) {

            Log.d("up", angle+"");

            makeAngle(0, 180);
            ystep = 0;
        }

        // 오른쪽벽 부딧친 경우
        if (xstep + size > getWidth()) { //xstep < 0

            Log.d("right", angle+"");
            makeAngle(90,180);
            xstep = getWidth() - size;


        }

        //아래쪽 벽
        if (ystep + size > getHeight()) {

            Log.d("down", angle+"");
            makeAngle(180, 180);
            ystep = getHeight() - size; // 벽에 들어가버리는것 방지
        }
        Movement mm= new Movement(angle);
        xMov=mm.xMov;
        yMov=mm.yMov;

        xstep += xMov; // 네모 이
        ystep += yMov; // 오른쪽 벽에 부딧친 경우

        rect.left = xstep;
        rect.top = ystep;
        rect.right = rect.left + size;
        rect.bottom = rect.top + size;

        int n;
        for (int i = 0; i < blocks.length; i++) {
            if (blocks[i].Box_Exit) {

                n = blocks[i].isCrash(rect);    //벽돌 부수기
                if (n == 1) { // 블록이 위에있는 경우 위쪽으로 튀게한다.
                    makeAngle(0, 180);
                    blocks[i].breakBlock();
                    break;
                } else if (n == 2) { // 블록이 아래있는경우 아래쪽으로 튀게한다.
                    makeAngle(180, 180);
                    blocks[i].breakBlock();
                    break;
                }
            }
        }

        // 바와 공이 겹칠경우 윗쪽으로 튀게 한다
        if (Rect.intersects(barRect, rect)) {
            makeAngle(180, 180);
        }

        canvas.drawRect(rect, pnt); // 공 그리기
        canvas.drawRect(xBar - barWidth / 2, yBar, xBar + barWidth, yBar + 10, pnt);
        pnt.setColor(Color.YELLOW);
        for (int i = 0; i < blocks.length; i++) {
            if (blocks[i].Box_Exit) {
                canvas.drawRect(blocks[i].Box_Rect, pnt);
            }
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {


            // 바의 중앙보다 터치위치가 더 오른쪽이면
            if (event.getX() > xBar + barWidth / 2)
                xBar += 60; // 바를 오른쪽으로 이동
            else
                xBar -= 60; // 바를 왼쪽으로 이동

            barRect.left = xBar;
            barRect.top = yBar;
            barRect.right = barRect.left + barWidth;
            barRect.bottom = barRect.top + barHeight; // 바의 사각영역 설정
            return true;

        }
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            return true;
        }
        return false;
    }
}
