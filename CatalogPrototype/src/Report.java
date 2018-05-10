import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.datatable.DataTable;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Report {

    private ArrayList<ReportRow> rows = new ArrayList<>();

    public void addRow(ReportRow row) {
        rows.add(row);
    }

    public void generatePDF(String filename) throws IOException {
        List<List> data = new ArrayList<>();
        data.add(Arrays.asList("Nr. crt.", "Numele si prenumele", "Media la proba 1", "Media la proba 2",
                "Media finala a ex de licenta", "Observatii"));
        for (ReportRow row : rows) {
            String observatii = row.calculateAverage() >= 5.0 ? "promovat" : "nepromovat";
            DecimalFormat format = new DecimalFormat("#.00");
            data.add(Arrays.asList(data.size(),
                    (row.getStudent().getNume() + " " + row.getStudent().getPrenume()).toUpperCase(),
                    format.format(row.calculateAverage(Task.TASK1)), format.format(row.calculateAverage(Task.TASK2)),
                    format.format(row.calculateAverage()), observatii));
        }

        PDDocument doc = new PDDocument();
        PDPage page = new PDPage();
        page.setMediaBox(new PDRectangle(PDRectangle.A4.getWidth(), PDRectangle.A4.getHeight()));
        doc.addPage(page);

        float margin = 10;
        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
        float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
        float yStart = yStartNewPage;
        float bottomMargin = 20;
        BaseTable baseTable = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, doc, page, true, true);
        DataTable dataTable = new DataTable(baseTable, page);

        dataTable.addListToTable(data, true);
        baseTable.draw();
        doc.save(new File(filename));
    }
}
