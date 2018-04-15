package de.jofre.table;

public enum DiagramType {
	BUBBLE_CHART("Bubble-Chart"),
	CALENDAR_CHART("Calendar-Chart"),
	CHORD_CHART("Chord-Chart"),
	CHOROPLETH("Choropletenkarte"),
	COLLAPSIBLE_INTENDED_TREEVIEW("Collapsible intended treeview"),
	COLLAPSIBLE_TREEVIEW("Collapsible treeview"),
	FLARE_CHART("Flare-Chart"),
	GLOBE_CHART("Globe-Chart"),
	HIERARCHY_BAR("Hierarchy-Bar"),
	LINE_CHART("Line-Chart"),
	SUNBURST_CHART("Sunburst-Chart"),
	WORD_CLOUD("Word-Cloud"),
	ZOOMABLE_TREEMAP("Zoomable-Treemap");
	
	private String name;
	
    private DiagramType(String name) {
        this.name = name;
    }
    
    public String getName() {
		return name;
	}
    
    public static DiagramType getTypeByName(String text) {
    	if (text.equals(BUBBLE_CHART.name)) return BUBBLE_CHART;
    	if (text.equals(CALENDAR_CHART.name)) return CALENDAR_CHART;
    	if (text.equals(CHORD_CHART.name)) return CHORD_CHART;
    	if (text.equals(CHOROPLETH.name)) return CHOROPLETH;
    	if (text.equals(COLLAPSIBLE_INTENDED_TREEVIEW.name)) return COLLAPSIBLE_INTENDED_TREEVIEW;
    	if (text.equals(COLLAPSIBLE_TREEVIEW.name)) return COLLAPSIBLE_TREEVIEW;
    	if (text.equals(FLARE_CHART.name)) return FLARE_CHART;
    	if (text.equals(GLOBE_CHART.name)) return GLOBE_CHART;
    	if (text.equals(HIERARCHY_BAR.name)) return HIERARCHY_BAR;
    	if (text.equals(LINE_CHART.name)) return LINE_CHART;
    	if (text.equals(SUNBURST_CHART.name)) return SUNBURST_CHART;
    	if (text.equals(WORD_CLOUD.name)) return WORD_CLOUD;
    	if (text.equals(ZOOMABLE_TREEMAP.name)) return ZOOMABLE_TREEMAP;
    	return null;
    }
}
