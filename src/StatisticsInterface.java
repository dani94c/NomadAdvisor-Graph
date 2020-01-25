import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;

public class StatisticsInterface {

    @FXML
    private PieChart citiesPieChart;

    @FXML
    private PieChart customerPieChart;
    
    @FXML
    private PieChart averageAgePieChart;
    
    @FXML
    private Label outcomeLabel;
    
    // Creates the slices of the pie chart using the key of the map as label and the value of the map as value of the slice
    private ObservableList<PieChart.Data> setPieChartData(HashMap<String, Integer> slices) {
    	ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
    	for(Map.Entry<String, Integer> slice : slices.entrySet()) {
    		if(slice.getKey() == Utils.cityAttributes.get(Utils.cityNames.TEMPERATURE))
    			pieChartData.add(new PieChart.Data("Temperature", slice.getValue()));
    		if(slice.getKey() == Utils.cityAttributes.get(Utils.cityNames.AIR_QUALITY))
    			pieChartData.add(new PieChart.Data("Air quality", slice.getValue()));
    		if(slice.getKey() == Utils.cityAttributes.get(Utils.cityNames.COST))
    			pieChartData.add(new PieChart.Data("Cost", slice.getValue()));
    		if(slice.getKey() == Utils.cityAttributes.get(Utils.cityNames.SAFETY))
    			pieChartData.add(new PieChart.Data("Safety", slice.getValue()));
    		if(slice.getKey() == Utils.cityAttributes.get(Utils.cityNames.QUALITY_LIFE))
    			pieChartData.add(new PieChart.Data("Quality of life", slice.getValue()));
    		if(slice.getKey() == Utils.cityAttributes.get(Utils.cityNames.WALKABILITY))
    			pieChartData.add(new PieChart.Data("Walkability", slice.getValue()));
    		if(slice.getKey() == Utils.cityAttributes.get(Utils.cityNames.HEALTHCARE))
    			pieChartData.add(new PieChart.Data("Healthcare", slice.getValue()));
    		if(slice.getKey() == Utils.cityAttributes.get(Utils.cityNames.NIGHTLIFE))
    			pieChartData.add(new PieChart.Data("Nightlife", slice.getValue()));
    		if(slice.getKey() == Utils.cityAttributes.get(Utils.cityNames.WIFI))
    			pieChartData.add(new PieChart.Data("Free wifi", slice.getValue()));
    		if(slice.getKey() == Utils.cityAttributes.get(Utils.cityNames.FOREIGNERS))
    			pieChartData.add(new PieChart.Data("Friendly for foreigners", slice.getValue()));
    		if(slice.getKey() == Utils.cityAttributes.get(Utils.cityNames.ENGLISH))
    			pieChartData.add(new PieChart.Data("English speaking", slice.getValue()));
    	}
    	return pieChartData;
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
    
    // Computes the data to show and inserts them in the pie charts
    public void initInterface() {
    	outcomeLabel.setText("");
    	List<HashMap<String, Integer>> pieChartsData = NomadHandler.computePieChartsData();
    	if(pieChartsData == null) {
    		outcomeLabel.setText("An error occurred during data loading");
    		return;
    	}
    	this.setCityPieChart(pieChartsData.get(0));
    	this.setCustomerPieChart(pieChartsData.get(1));
    }

}
