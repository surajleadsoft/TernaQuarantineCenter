package com.futuregenerations.ternaquarantinecenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class PdfFooter extends PdfPageEventHelper {

    int pageNumber;
    Context context;
    public PdfFooter(Context context) {
        this.context = context;
    }

    @Override
    public void onOpenDocument(PdfWriter writer, Document document) {
    }

    @Override
    public void onChapter(PdfWriter writer, Document document, float paragraphPosition, Paragraph title) {
        pageNumber = 1;
    }

    @Override
    public void onStartPage(PdfWriter writer, Document document) {
        pageNumber++;
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        Rectangle rectangle = writer.getPageSize();

        try {
            InputStream ins = context.getAssets().open("footer_pdf.jpg");
            Bitmap bitmap = BitmapFactory.decodeStream(ins);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG,100, stream);
            Image image = Image.getInstance(stream.toByteArray());
            image.setWidthPercentage(100);
            image.scaleAbsolute(rectangle.getWidth(),20);
            image.setAbsolutePosition(rectangle.getBottom(),rectangle.getBottom());
            document.add(image);

        } catch (Exception e) {
            e.printStackTrace();
        }
        ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_RIGHT, new Phrase(String.valueOf(pageNumber),new Font(Font.FontFamily.HELVETICA,12,Font.BOLD, BaseColor.WHITE)),rectangle.getRight()-5
        ,rectangle.getBottom()+5,0);
        ColumnText.showTextAligned(writer.getDirectContent(),Element.ALIGN_LEFT,new Phrase("Powered By, LeadSoft IT Solutions, Osmanabad",new Font(Font.FontFamily.HELVETICA,12,Font.BOLD, BaseColor.WHITE)),
                rectangle.getLeft()+5,rectangle.getBottom()+5,0);
    }
}
