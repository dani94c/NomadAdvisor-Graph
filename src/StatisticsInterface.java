import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Label;

public class StatisticsInterface {

    @FXML
    private PieChart citiesPieChart;

    @FXML
    private PieChart customerPieChart;
    
    @FXML
    private BarChart<String,Integer> avgAgeChart;
    
    @FXML
    private Label outcomeLabel;
    
    // Creates the slices of the pie chart using the key of the map as label and the value of the map as value of the slice
    private ObservableList<PieChart.Data> setPieChartData(HashMap<String, Integer> slices) {
    	ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
    	for(Map.Entry<String, Integer> slice : slices.entrySet()) {
    		if(slice.getKey().equals(Utils.cityAttributes.get(Utils.cityNames.TEMPERATURE)))
    			pieChartData.add(new PieChart.Data("Temperature", slice.getValue()));
    		if(slice.getKey().equals(Utils.cityAttributes.get(Utils.cityNames.AIR_QUALITY)))
    			pieChartData.add(new PieChart.Data("Air quality", slice.getValue()));
    		if(slice.getKey().equals(Utils.cityAttributes.get(Utils.cityNames.COST)))
    			pieChartData.add(new PieChart.Data("Cost", slice.getValue()));
    		if(slice.getKey().equals(Utils.cityAttributes.get(Utils.cityNames.SAFETY)))
    			pieChartData.add(new PieChart.Data("Safety", slice.getValue()));
    		if(slice.getKey().equals(Utils.cityAttributes.get(Utils.cityNames.QUALITY_LIFE)))
    			pieChartData.add(new PieChart.Data("Quality of life", slice.getValue()));
    		if(slice.getKey().equals(Utils.cityAttributes.get(Utils.cityNames.WALKABILITY)))
    			pieChartData.add(new PieChart.Data("Walkability", slice.getValue()));
    		if(slice.getKey().equals(Utils.cityAttributes.get(Utils.cityNames.HEALTHCARE)))
    			pieChartData.add(new PieChart.Data("Healthcare", slice.getValue()));
    		if(slice.getKey().equals(Utils.cityAttributes.get(Utils.cityNames.NIGHTLIFE)))
    			pieChartData.add(new PieChart.Data("Nightlife", slice.getValue()));
    		if(slice.getKey().equals(Utils.cityAttributes.get(Utils.cityNames.WIFI)))
    			pieChartData.add(new PieChart.Data("Free wifi", slice.getValue()));
    		if(slice.getKey().equals(Utils.cityAttributes.get(Utils.cityNames.FOREIGNERS)))
    			pieChartData.add(new PieChart.Data("Friendly for foreigners", slice.getValue()));
    		if(slice.getKey().equals(Utils.cityAttributes.get(Utils.cityNames.ENGLISH)))
    			pieChartData.add(new PieChart.Data("English speaking", slice.getValue()));
    	}
    	return pieChartData;
    }
    
 // Creates the slices of the pie chart using the key of the map as label and the value of the map as value of the slice
    private Series<String,Integer> setBarChartData(HashMap<String, Integer> bars) {
    	Series<String,Integer> barChartData = new XYChart.Series();
    	for(Map.Entry<String, Integer> bar : bars.entrySet()) {
    		if(bar.getKey().equals(Utils.cityAttributes.get(Utils.cityNames.TEMPERATURE)))
    			barChartData.getData().add(new XYChart.Data("Temperature", bar.getValue()));
    		if(bar.getKey().equals(Utils.cityAttributes.get(Utils.cityNames.AIR_QUALITY)))
    			barChartData.getData().add(new BarChart.Data("Air quality", bar.getValue()));
    		if(bar.getKey().equals(Utils.cityAttributes.get(Utils.cityNames.COST)))
    			barChartData.getData().add(new BarChart.Data("Cost", bar.getValue()));
    		if(bar.getKey().equals(Utils.cityAttributes.get(Utils.cityNames.SAFETY)))
    			barChartData.getData().add(new BarChart.Data("Safety", bar.getValue()));
    		if(bar.getKey().equals(Utils.cityAttributes.get(Utils.cityNames.QUALITY_LIFE)))
    			barChartData.getData().add(new BarChart.Data("Quality of life", bar.getValue()));
    		if(bar.getKey().equals(Utils.cityAttributes.get(Utils.cityNames.WALKABILITY)))
    			barChartData.getData().add(new BarChart.Data("Walkability", bar.getValue()));
    		if(bar.getKey().equals(Utils.cityAttributes.get(Utils.cityNames.HEALTHCARE)))
    			barChartData.getData().add(new BarChart.Data("Healthcare", bar.getValue()));
    		if(bar.getKey().equals(Utils.cityAttributes.get(Utils.cityNames.NIGHTLIFE)))
    			barChartData.getData().add(new BarChart.Data("Nightlife", bar.getValue()));
    		if(bar.getKey().equals(Utils.cityAttributes.get(Utils.cityNames.WIFI)))
    			barChartData.getData().add(new BarChart.Data("Free wifi", bar.getValue()));
    		if(bar.getKey().equals(Utils.cityAttributes.get(Utils.cityNames.FOREIGNERS)))
    			barChartData.getData().add(new BarChart.Data("Friendly for foreigners", bar.getValue()));
    		if(bar.getKey().equals(Utils.cityAttributes.get(Utils.cityNames.ENGLISH)))
    			barChartData.getData().add(new BarChart.Data("English speaking", bar.getValue()));
    	}
    	return barChartData;
    }
    
    // Gets the data from the map and inserts them in the cities pie chart
    private void setCityPieChart(HashMap<String, Integer> slices) {
    	ObservableList<PieChart.Data> pieChartData = setPieChartData(slices);
    	citiesPieChart.setData(pieChartData);
    }
    
    // Gets the data from the map and inserts them in the customers pie chart
    private void setCustomerPieChart(HashMap<String, Integer> slices) {
    	ObservableList<PieChart.Data> pieChartData = setPieChartData(slices);
    	customerPieChart.setData(pieChartData);
    }
    
    // Get the data from the map and inserts them in the cities pie chart
    private void setAverageAgeBarChart(HashMap<String, Integer> bars) {
    	Series<String, Integer> pieChartData = setBarChartData(bars);
    	avgAgeChart.getData().add(pieChartData);
    }
    
    // Computes the data to show and inserts them in the pie charts
    public void initInterface() {
    	//this.avgAgeChart.getData().clear();
    	List<HashMap<String, Integer>> chartsData = NomadHandler.computeChartsData();
    	if(chartsData == null) {
    		outcomeLabel.setText("An error occurred during data loading");
    		return;
    	}
    	this.setCityPieChart(chartsData.get(0));
    	this.setCustomerPieChart(chartsData.get(1));
    	this.setAverageAgeBarChart(chartsData.get(2));
    }
    
    public void cleanInt() {
    	outcomeLabel.setText("");
    	this.avgAgeChart.getData().clear();
    	this.citiesPieChart.getData().clear();
    	this.customerPieChart.getData().clear();
    }

}
