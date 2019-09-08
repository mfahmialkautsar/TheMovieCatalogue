//package ga.softogi.themoviecatalogue.util;
//
//import android.content.Context;
//import android.graphics.Rect;
//import android.support.annotation.NonNull;
//import android.support.v7.widget.RecyclerView;
//import android.view.View;
//
//public class GridMarginDecor extends RecyclerView.ItemDecoration {
//
//    private int left;
//    private int top;
//    private int right;
//    private int bottom;
//
//    public GridMarginDecor(Context context, int left, int top, int right, int bottom) {
//        this.left = left;
//        this.top = top;
//        this.right = right;
//        this.bottom = bottom;
//    }
//
//    @Override
//    public void getItemOffsets(@NonNull Rect outRect,@NonNull View view,@NonNull RecyclerView parent,@NonNull RecyclerView.State state) {
//        outRect.set(left, top, right, bottom);
//    }
//}
