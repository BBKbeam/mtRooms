package bbk_beam.mtRooms.ui.model.revenue;

import bbk_beam.mtRooms.reservation.dto.PaymentMethod;
import bbk_beam.mtRooms.revenue.dto.DetailedPayment;
import bbk_beam.mtRooms.ui.model.common.DateStamp;
import bbk_beam.mtRooms.ui.model.common.MonthStamp;
import bbk_beam.mtRooms.ui.model.common.YearStamp;
import eadjlib.logger.Logger;
import javafx.scene.chart.*;
import javafx.scene.chart.XYChart.Data;

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.*;

public class RevenuesChart {
    private final Logger log = Logger.getLoggerInstance(RevenuesChart.class.getName());
    private static final Locale LOCALE = Locale.getDefault();
    private ResourceBundle resourceBundle;
    private Calendar calendar = Calendar.getInstance();
    private Date min_timestamp;
    private Date max_timestamp;
    private Map<PaymentMethod, Double> method_data = new HashMap<>();
    private Map<Integer, Double> hourOfDay_data = new TreeMap<>();
    private Map<Integer, Double> dayOfWeek_data = new TreeMap<>();
    private Map<Integer, Double> weekOfYear_data = new TreeMap<>();
    private Map<DateStamp, Double> daily_data = new TreeMap<>();
    private Map<MonthStamp, Double> monthly_data = new TreeMap<>();
    private Map<YearStamp, Double> yearly_data = new TreeMap<>();

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

    private void addDailyData(Date timestamp, Double amount) {
        if (this.daily_data.isEmpty()) {
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(this.min_timestamp);

            while (!calendar.getTime().after(max_timestamp)) {
                Date date = calendar.getTime();
                this.daily_data.put(new DateStamp(date), 0d);
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }
        }
        DateStamp day = new DateStamp(timestamp);
        Double new_amount = (this.daily_data.get(day) != null ? this.daily_data.get(day) + amount : amount);
        this.daily_data.put(day, new_amount);
    }

    private void addMonthlyData(Date timestamp, Double amount) {
        if (this.monthly_data.isEmpty()) {
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(this.min_timestamp);

            while (!calendar.getTime().after(max_timestamp)) {
                Date date = calendar.getTime();
                this.daily_data.put(new DateStamp(date), 0d);
                calendar.add(Calendar.MONTH, 1);
            }
        }
        MonthStamp month = new MonthStamp(timestamp);
        Double new_amount = (this.monthly_data.get(month) != null ? this.monthly_data.get(month) + amount : amount);
        this.monthly_data.put(month, new_amount);
    }

    private void addYearlyData(Date timestamp, Double amount) {
        if (this.monthly_data.isEmpty()) {
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(this.min_timestamp);

            while (!calendar.getTime().after(max_timestamp)) {
                Date date = calendar.getTime();
                this.daily_data.put(new DateStamp(date), 0d);
                calendar.add(Calendar.YEAR, 1);
            }
        }
        YearStamp year = new YearStamp(timestamp);
        Double new_amount = (this.yearly_data.get(year) != null ? this.yearly_data.get(year) + amount : amount);
        this.yearly_data.put(year, new_amount);
    }

    /**
     * Constructor
     *
     * @param resources ResourceBundle instance
     * @param data      List of DetailedPayment DTOs
     */
    public RevenuesChart(ResourceBundle resources, List<DetailedPayment> data) {
        this.resourceBundle = resources;

        if (!data.isEmpty()) {
            this.min_timestamp = data.get(0).timestamp();
            this.max_timestamp = data.get(data.size() - 1).timestamp();
            for (DetailedPayment payment : data) {
                System.out.println(payment.timestamp());
                addMethodData(payment.paymentMethod(), payment.amount());
                addHourlyData(payment.timestamp(), payment.amount());
                addWeekdayData(payment.timestamp(), payment.amount());
                addWeekOfYearData(payment.timestamp(), payment.amount());
                addDailyData(payment.timestamp(), payment.amount());
                addMonthlyData(payment.timestamp(), payment.amount());
                addYearlyData(payment.timestamp(), payment.amount());
            }
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

    public LineChart getDailyLineChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (Map.Entry<DateStamp, Double> entry : this.daily_data.entrySet()) {
            series.getData().add(
                    new Data<>(entry.getKey().toString(), entry.getValue())
            );
        }
        CategoryAxis x = new CategoryAxis();
        NumberAxis y = new NumberAxis();
        x.setLabel(this.resourceBundle.getString("Chart_X_DayDate"));
        y.setLabel(this.resourceBundle.getString("Chart_Y_Amount"));
        x.setTickLabelRotation(65);
        x.tickLabelRotationProperty().set(0d);
        LineChart<String, Number> chart = new LineChart<>(x, y);
        chart.setTitle(this.resourceBundle.getString("Chart_Title_DailyRevenue"));
        chart.getData().add(series);
        chart.setLegendVisible(false);
        return chart;
    }

    public BarChart getMonthlyBarChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (Map.Entry<MonthStamp, Double> entry : this.monthly_data.entrySet()) {
            series.getData().add(
                    new Data<>(String.valueOf(entry.getKey()), entry.getValue())
            );
        }
        CategoryAxis x = new CategoryAxis();
        NumberAxis y = new NumberAxis();
        x.setLabel(this.resourceBundle.getString("Chart_X_MonthYear"));
        y.setLabel(this.resourceBundle.getString("Chart_Y_Amount"));
        BarChart<String, Number> chart = new BarChart<>(x, y);
        chart.setTitle(this.resourceBundle.getString("Chart_Title_MonthlyRevenue"));
        chart.getData().add(series);
        chart.setLegendVisible(false);
        return chart;
    }

    public BarChart getYearlyBarChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (Map.Entry<YearStamp, Double> entry : this.yearly_data.entrySet()) {
            series.getData().add(
                    new Data<>(String.valueOf(entry.getKey()), entry.getValue())
            );
        }
        CategoryAxis x = new CategoryAxis();
        NumberAxis y = new NumberAxis();
        x.setLabel(this.resourceBundle.getString("Chart_X_Year"));
        y.setLabel(this.resourceBundle.getString("Chart_Y_Amount"));
        BarChart<String, Number> chart = new BarChart<>(x, y);
        chart.setTitle(this.resourceBundle.getString("Chart_Title_YearlyRevenue"));
        chart.getData().add(series);
        chart.setLegendVisible(false);
        return chart;
    }
}
