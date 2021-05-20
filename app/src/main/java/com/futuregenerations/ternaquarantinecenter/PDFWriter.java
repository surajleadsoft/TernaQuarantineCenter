package com.futuregenerations.ternaquarantinecenter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import androidx.core.content.FileProvider;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class PDFWriter {

    Context context;

    public PDFWriter(Context context) {
        this.context = context;
    }

    public HashMap<String,Object> createAutoPDF(List<AutoMailDetails> detailsList) throws FileNotFoundException, DocumentException {

        HashMap<String,Object> objectHashMap = new HashMap<>();
        boolean isDir;
        File pdfFile;
        File documentFolder = new File(Environment.getExternalStorageDirectory() + File.separator + context.getResources().getString(R.string.app_name) + File.separator + "BackUp");

        if (!documentFolder.exists()) {
            isDir = documentFolder.mkdirs();
        }

        else {
            isDir = true;
        }

        if (isDir) {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
            String pdfName = "Report_File_" + dateFormat.format(calendar.getTime())+ ".pdf";
            pdfFile = new File(documentFolder.getAbsolutePath(), pdfName );
            OutputStream stream = new FileOutputStream(pdfFile);
            Document document = new Document(PageSize.A4.rotate(),2,2,20,30);

            PdfPTable pdfPTable = new PdfPTable(new float[]{4, 3, 2, 6, 2, 4, 3, 3, 3, 3});
            pdfPTable.setTotalWidth(PageSize.A4.getHeight());
            pdfPTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            pdfPTable.getDefaultCell().setPaddingTop(5);
            pdfPTable.getDefaultCell().setPaddingBottom(5);
            pdfPTable.setWidthPercentage(100);
            pdfPTable.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
            pdfPTable.getFooter().setRole(PdfName.WATERMARK);

            pdfPTable.addCell("Name");
            pdfPTable.addCell("Contact No.");
            pdfPTable.addCell("Gender");
            pdfPTable.addCell("Address");
            pdfPTable.addCell("Age");
            pdfPTable.addCell("Adhar Card");
            pdfPTable.addCell("Remark");
            pdfPTable.addCell("Center Type");
            pdfPTable.addCell("Center Name");
            pdfPTable.addCell("Created At");

            pdfPTable.setHeaderRows(1);

            for (int i = 0; i < detailsList.size(); i++) {

                String name = detailsList.get(i).getName();
                String contactNo = detailsList.get(i).getContactNo();
                String gender = detailsList.get(i).getGender();
                String address = detailsList.get(i).getAddress();
                String remark = detailsList.get(i).getRemark();
                String centerType = detailsList.get(i).getCenterType();
                String centerName = detailsList.get(i).getCenterName();
                String adharCard = detailsList.get(i).getAdharCard();
                String age = detailsList.get(i).getAge();
                String createdAt = detailsList.get(i).getCreatedAt();

                pdfPTable.addCell(name);
                pdfPTable.addCell(contactNo);
                pdfPTable.addCell(gender);
                pdfPTable.addCell(address);
                pdfPTable.addCell(age);
                pdfPTable.addCell(adharCard);
                pdfPTable.addCell(remark);
                pdfPTable.addCell(centerType);
                pdfPTable.addCell(centerName);
                pdfPTable.addCell(createdAt);
            }

            PdfWriter writer = PdfWriter.getInstance(document, stream);
            writer.setPageEvent(new PdfFooter(context));
            document.open();
            Font font = new Font(Font.FontFamily.TIMES_ROMAN,40,Font.BOLD, BaseColor.BLACK);
            Paragraph paragraphSpace = new Paragraph("\n\n");
            Paragraph header = new Paragraph(context.getResources().getString(R.string.app_name),font);
            header.setAlignment(Element.ALIGN_CENTER);
            document.add(header);
            document.add(paragraphSpace);
            document.add(new Paragraph("Date : "+new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault()).format(Calendar.getInstance().getTime()),new Font(Font.FontFamily.TIMES_ROMAN,20,Font.NORMAL,BaseColor.BLACK)));
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Filtered By : Date",new Font(Font.FontFamily.TIMES_ROMAN,20,Font.NORMAL,BaseColor.BLACK)));
            document.add(paragraphSpace);
            document.add(pdfPTable);

            document.close();

            objectHashMap.clear();
            objectHashMap.put("name",pdfName);
            objectHashMap.put("success",true);
            objectHashMap.put("path",pdfFile);
        }
        else {
            objectHashMap.clear();
            objectHashMap.put("success",false);
            objectHashMap.put("path",null);
        }
        return objectHashMap;
    }

    public HashMap<String,Object> createManualPDF(List<SearchDetails> searchDetailsList, String searchedBy) throws FileNotFoundException, DocumentException {
        int count;
        HashMap<String,Object> objectHashMap = new HashMap<>();
        boolean isDir;
        File pdfFile;
        File documentFolder = new File(Environment.getExternalStorageDirectory() + File.separator + context.getResources().getString(R.string.app_name) + File.separator + "Documents");

        if (!documentFolder.exists()) {
            isDir = documentFolder.mkdirs();
        }

        else {
            isDir = true;
        }

        if (isDir) {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
            String pdfName = "Report_File_" + dateFormat.format(calendar.getTime())+ ".pdf";
            pdfFile = new File(documentFolder.getAbsolutePath(), pdfName );
            OutputStream stream = new FileOutputStream(pdfFile);
            Document document = new Document(PageSize.A4.rotate(),2,2,10,30);

            PdfPTable pdfPTable = new PdfPTable(new float[]{1, 4, 3, 1, 3, 4, 3, 4});
            pdfPTable.setTotalWidth(PageSize.A4.getHeight());
            pdfPTable.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            pdfPTable.getDefaultCell().setPaddingBottom(5);
            pdfPTable.getDefaultCell().setPaddingTop(5);
            pdfPTable.setWidthPercentage(100);
            pdfPTable.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
            Font font1 = new Font(Font.FontFamily.TIMES_ROMAN,15,Font.BOLD,BaseColor.BLACK);
            Paragraph phraseName = new Paragraph("Name",font1);
            pdfPTable.addCell(new Paragraph("Sr No.",font1));
            pdfPTable.addCell(phraseName);
            pdfPTable.addCell(new Paragraph("Date",font1));
            pdfPTable.addCell(new Paragraph("Age",font1));
            pdfPTable.addCell(new Paragraph("Adhar Card",font1));
            pdfPTable.addCell(new Paragraph("Remark",font1));
            pdfPTable.addCell(new Paragraph("Center Type",font1));
            pdfPTable.addCell(new Paragraph("Center Name",font1));

            pdfPTable.setHeaderRows(1);

            for (int i = 0; i < searchDetailsList.size(); i++) {

                String name = searchDetailsList.get(i).getName();
                String date = searchDetailsList.get(i).getDate();
                String remark = searchDetailsList.get(i).getRemark();
                String centerType = searchDetailsList.get(i).getCenterType();
                String centerName = searchDetailsList.get(i).getCenter();
                String adharCard = searchDetailsList.get(i).getAdhar();
                String age = searchDetailsList.get(i).getAge();
                count = i+1;
                pdfPTable.addCell(new Paragraph(String.valueOf(count),font1));
                pdfPTable.addCell(name);
                pdfPTable.addCell(date);
                pdfPTable.addCell(age);
                pdfPTable.addCell(adharCard);
                pdfPTable.addCell(remark);
                pdfPTable.addCell(centerType);
                pdfPTable.addCell(centerName);
            }

            PdfWriter writer = PdfWriter.getInstance(document, stream);
            writer.setPageEvent(new PdfFooter(context));
            document.open();

            Font font = new Font(Font.FontFamily.TIMES_ROMAN,25,Font.BOLD, BaseColor.BLACK);
            Paragraph paragraphSpace = new Paragraph("\n");
            Paragraph header = new Paragraph(context.getResources().getString(R.string.app_name),font);
            header.setAlignment(Element.ALIGN_CENTER);
            document.add(header);
            document.add(paragraphSpace);
            document.add(new Phrase("Date : "+new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault()).format(Calendar.getInstance().getTime()),
                    new Font(Font.FontFamily.TIMES_ROMAN,13,Font.NORMAL,BaseColor.BLACK)));
            document.add(new Phrase("\n"));
            document.add(new Phrase("Filtered By : "+ searchedBy,new Font(Font.FontFamily.TIMES_ROMAN,13,Font.NORMAL,BaseColor.BLACK)));
            document.add(new Phrase("\n"));
            document.add(pdfPTable);

            document.close();

            viewGeneratedPDF(pdfFile);

            objectHashMap.clear();
            objectHashMap.put("name",pdfName);
            objectHashMap.put("success",true);
            objectHashMap.put("path",pdfFile);
        }
        else {
            objectHashMap.clear();
            objectHashMap.put("success",false);
            objectHashMap.put("path",null);
        }
        return objectHashMap;
    }

    private void viewGeneratedPDF(File pdfFile) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID+".provider",pdfFile);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(uri,"application/pdf");
        context.startActivity(intent);
    }
}
