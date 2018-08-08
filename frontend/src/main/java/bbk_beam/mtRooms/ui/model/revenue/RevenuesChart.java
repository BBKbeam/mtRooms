package bbk_beam.mtRooms.ui.model.revenue;

import bbk_beam.mtRooms.reservation.dto.PaymentMethod;
import bbk_beam.mtRooms.revenue.dto.DetailedPayment;
import eadjlib.logger.Logger;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.*;

public class RevenuesChart {
    private final Logger log = Logger.getLoggerInstance(RevenuesChart.class.getName());
    private static final Locale LOCALE = Locale.getDefault();
    private ResourceBundle resourceBundle;
    private Calendar calendar = Calendar.getInstance();
    private Map<PaymentMethod, Double> method_data = new HashMap<>();
    private Map<Integer, Double> hourOfDay_data = new TreeMap<>();
    private Map<Integer, Double> dayOfWeek_data = new TreeMap<>();
    private Map<Integer, Double> weekOfYear_data = new TreeMap<>();
    private Map<Integer, Double> dayOfMonth_data = new TreeMap<>();
    private Map<Integer, Double> monthOfYear_data = new TreeMap<>();

    private void addMethodData(PaymentMethod method, Double amount) {
        Double new_amount = (this.method_data.get(method) != null ? this.method_data.get(method) + amount : amount);
        this.method_data.put(method, new_amount);
    }

    private void addHourlyData(Date timestamp, Double amount) {
        if (this.hourOfDay_data.isEmpty()) {
            for (int i = 0; i < 24; i++)
                this.hourOfDay_data.put(i, 0d);
        }
        calendar.setTime(timestamp);
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        Double new_amount = (this.hourOfDay_data.get(hourOfDay) != null ? this.hourOfDay_data.get(hourOfDay) + amount : amount);
        this.hourOfDay_data.put(hourOfDay, new_amount);
    }

    private void addWeekdayData(Date timestamp, Double amount) {
        if (this.dayOfWeek_data.isEmpty()) {
            for (int i = 1; i <= 7; i++)
                this.dayOfWeek_data.put(i, 0d);
        }
        calendar.setTime(timestamp);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        Double new_amount = (this.dayOfWeek_data.get(dayOfWeek) != null ? this.dayOfWeek_data.get(dayOfWeek) + amount : amount);
        this.dayOfWeek_data.put(dayOfWeek, new_amount);
    }

    private void addWeekOfYearData(Date timestamp, Double amount) {
        if (this.weekOfYear_data.isEmpty()) {
            for (int i = 1; i <= 52; i++)
                this.weekOfYear_data.put(i, 0d);
        }
        calendar.setTime(timestamp);
        int weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR);
        Double new_amount = (this.weekOfYear_data.get(weekOfYear) != null ? this.weekOfYear_data.get(weekOfYear) + amount : amount);
        this.weekOfYear_data.put(weekOfYear, new_amount);
    }

//    private void addDailyData(Date timestamp, Double amount) {
//        static SimpleDateFormat dateOnly = new SimpleDateFormat("MM/dd/yyyy");
//        String date =
//        int dayOfMonth = calendar.get(Calendar.WEEK_OF_YEAR);
//        Double new_amount = (this.dayOfMonth_data.get(dayOfMonth) != null ? this.dayOfMonth_data.get(dayOfMonth) + amount : amount);
//        this.dayOfMonth_data.put(dayOfMonth, new_amount);
//    }
//
//    private void addMonthOfYearData(Date timestamp, Double amount) {
//        calendar.setTime(timestamp);
//        int monthOfYear = calendar.get(Calendar.MONTH);
//        Double new_amount = (this.monthOfYear_data.get(monthOfYear) != null ? this.monthOfYear_data.get(monthOfYear) + amount : amount);
//        this.monthOfYear_data.put(monthOfYear, new_amount);
//    }


    /**
     * Constructor
     *
     * @param resources ResourceBundle instance
     * @param data      List of DetailedPayment DTOs
     */
    public RevenuesChart(ResourceBundle resources, List<DetailedPayment> data) {
        this.resourceBundle = resources;

        for (DetailedPayment payment : data) {
            addMethodData(payment.paymentMethod(), payment.amount());
            addHourlyData(payment.timestamp(), payment.amount());
            addWeekdayData(payment.timestamp(), payment.amount());
            addWeekOfYearData(payment.timestamp(), payment.amount());
//            addDailyData(payment.timestamp(), payment.amount());
//            addMonthOfYearData(payment.timestamp(), payment.amount());
        }
    }

    public BarChart getMethodBarChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (Map.Entry<PaymentMethod, Double> entry : this.method_data.entrySet()) {
            series.getData().add(
                    new Data<>(entry.getKey().description(), entry.getValue())
            );
        }
        CategoryAxis x = new CategoryAxis();
        NumberAxis y = new NumberAxis();
        x.setLabel(this.resourceBundle.getString("Chart_X_Method"));
        y.setLabel(this.resourceBundle.getString("Chart_Y_Amount"));
        BarChart<String, Number> chart = new BarChart<>(x, y);
        chart.setTitle(this.resourceBundle.getString("Chart_Title_PaymentMethods"));
        chart.getData().add(series);
        chart.setLegendVisible(false);
        return chart;
    }

    public BarChart<String, Number> getHourlyBarChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (Map.Entry<Integer, Double> entry : this.hourOfDay_data.entrySet()) {
            series.getData().add(
                    new Data<>(String.valueOf(entry.getKey()), entry.getValue())
            );
        }
        CategoryAxis x = new CategoryAxis();
        NumberAxis y = new NumberAxis();
        x.setLabel(this.resourceBundle.getString("Chart_X_DayHour"));
        y.setLabel(this.resourceBundle.getString("Chart_Y_Amount"));
        BarChart<String, Number> chart = new BarChart<>(x, y);
        chart.setTitle(this.resourceBundle.getString("Chart_Title_HourlyRevenue"));
        chart.getData().add(series);
        chart.setLegendVisible(false);
        return chart;
    }

    public BarChart getWeekdayBarChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (Map.Entry<Integer, Double> entry : this.dayOfWeek_data.entrySet()) {
            series.getData().add(
                    new Data<>(DayOfWeek.of(entry.getKey()).getDisplayName(TextStyle.SHORT, LOCALE), entry.getValue())
            );
        }
        CategoryAxis x = new CategoryAxis();
        NumberAxis y = new NumberAxis();
        x.setLabel(this.resourceBundle.getString("Chart_X_Weekday"));
        y.setLabel(this.resourceBundle.getString("Chart_Y_Amount"));
        BarChart<String, Number> chart = new BarChart<>(x, y);
        chart.setTitle(this.resourceBundle.getString("Chart_Title_WeekdayRevenue"));
        chart.getData().add(series);
        chart.setLegendVisible(false);
        return chart;
    }

    public BarChart getWeekInYearBarChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (Map.Entry<Integer, Double> entry : this.weekOfYear_data.entrySet()) {
            series.getData().add(
                    new Data<>(String.valueOf(entry.getKey()), entry.getValue())
            );
        }
        CategoryAxis x = new CategoryAxis();
        NumberAxis y = new NumberAxis();
        x.setLabel(this.resourceBundle.getString("Chart_X_WeekOfYear"));
        y.setLabel(this.resourceBundle.getString("Chart_Y_Amount"));
        BarChart<String, Number> chart = new BarChart<>(x, y);
        chart.setTitle(this.resourceBundle.getString("Chart_Title_WeeklyRevenue"));
        chart.getData().add(series);
        chart.setLegendVisible(false);
        return chart;
    }
}
