package com.example.drawer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.Path;
import android.graphics.Bitmap;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MyDrawer extends View
{

    public Paint MyPaint;
    private List<MyPath>paths;
    private MyPath CurrentPath;
    private int CurrentColor;
    public int BrushSize;
    private class MyPath    //Создали свой класс
    {
        public Path path;   // свойства класса
        public int color;
        public int size;
        public MyPath (Path p, int c, int s)    // конструктор класса
        {

            path = p;
            color = c;
            size = s;
        }
    }

    //private Point LineStart;
    //private Point LineEnd;
    /*private class MyLine  // Класс, прямая линия
    {
        public Point Start;
        public Point End;
        public MyLine( int startX, int startY, int endX, int endY )
        {
            Start = new Point (startX, startY);
            End = new Point (endX, endY);
        }

    }
    /* */

      //private List<MyLine>linelist;  // список линий

    public MyDrawer(Context context, @Nullable AttributeSet attrs)
    {
        super(context, attrs); // вызывает конструктор (в данном случае view)
        init();

    }
    private void init()
    {
        CurrentColor = Color.BLACK;
        BrushSize = 5;
        paths = new ArrayList<MyPath>();
        MyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        MyPaint.setColor(CurrentColor);
        MyPaint.setStrokeWidth(BrushSize);
        MyPaint.setStyle(Paint.Style.STROKE);
        //pointList = new ArrayList<Point>();
        //linelist = new ArrayList<MyLine>();
        //MyPaint.setStrokeJoin(Paint.Join.ROUND);   //Выяснить для чего
        //MyPaint.setStrokeCap(Paint.Cap.ROUND);    //Выяснить для чего
    }


    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        for (MyPath pth : paths)
        {
            MyPaint.setColor(pth.color);
            MyPaint.setStrokeWidth(pth.size);
            canvas.drawPath(pth.path, MyPaint);  //рисование всех линий
        }
        MyPaint.setColor(CurrentColor);// рисование последней линии
        MyPaint.setStrokeWidth(BrushSize);
        if (CurrentPath != null) canvas.drawPath(CurrentPath.path, MyPaint);

        /*if (LineStart!=null && LineEnd!=null)   // Задаёт  рисование линий по списку
        canvas.drawLine(LineStart.x, LineStart.y, LineEnd.x, LineEnd.y, MyPaint);
        for (MyLine line : linelist)
        {
            canvas.drawLine(line.Start.x, line.Start.y, line.End.x, line.End.y, MyPaint);
        }

        /**/
    }

    public void Export ()
    {
        Bitmap bitmap = Bitmap.createBitmap(this.getWidth(), this.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint MyPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        MyPaint2.setColor(Color.WHITE);
        MyPaint2.setStyle(Paint.Style.FILL);
        canvas.drawPaint(MyPaint2);
        MyPaint2.setStyle(Paint.Style.STROKE);
        for (MyPath pth : paths)
        {
            MyPaint2.setColor(pth.color);
            MyPaint2.setStrokeWidth(pth.size);
            canvas.drawPath(pth.path, MyPaint2);  //рисование всех линий
        }
        try
        {
            String name = String.valueOf(Calendar.getInstance().getTimeInMillis());
            String FileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + name +".png" ;
            //String FileName = Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_PICTURES + "/" + name +".png" ;
            FileOutputStream out = new FileOutputStream(FileName);
            bitmap.compress(Bitmap.CompressFormat.PNG,100,out);
            Log.d("MyFile ",FileName);
            Context context = this.getContext();
            CharSequence text = "Файл сохранён " + FileName;
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            Context context = this.getContext();
            CharSequence text = "Не удалось сохранить файл";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) // Настройка касания
    {
        Log.d("MUCH EVENT X", String.valueOf(event.getX()));
        Log.d("MUCH EVENT Y", String.valueOf(event.getY()));

        if (event.getAction()==MotionEvent.ACTION_DOWN)
        {
          CurrentPath = new MyPath(new Path(), CurrentColor, BrushSize);    // вызываем конструктор, задает новый путь
          CurrentPath.path.moveTo(event.getX(), event.getY());   //задаёт начальную точку
            //  LineStart = new Point(Math.round(event.getX()), (Math.round(event.getY())));
        }

        else if (event.getAction()==MotionEvent.ACTION_MOVE)
        {
            if (CurrentPath != null) CurrentPath.path.lineTo(event.getX(), event.getY());    //рисует линию по движению пальца
            // LineEnd = new Point(Math.round(event.getX()), (Math.round(event.getY())));
        }

        else if (event.getAction()==MotionEvent.ACTION_UP)
        {
            paths.add(CurrentPath);    //добавление пути в список и запоминание пройденного пути
            CurrentPath = null;
          /*   if (LineStart != null && LineEnd != null) linelist.add(new MyLine(LineStart.x, LineStart.y, LineEnd.x, LineEnd.y));
            LineStart = null;
            LineEnd = null;

           */
        }



        //pointList.add(new Point(Math.round(event.getX()), Math.round(event.getY())));
        postInvalidate();

        return true;


    }

    public void setColor (int color)
    {
        CurrentColor = color;  // запоминание выбранного цвета

    }
    public void Undo ()
    {
       if (paths.size()>0)
       {
           paths.remove(paths.size() - 1);
           postInvalidate();
       }
    }


}
