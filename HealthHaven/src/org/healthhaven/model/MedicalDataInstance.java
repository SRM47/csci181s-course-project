/**
 * 
 */
package org.healthhaven.model;

import java.time.LocalDate;
import java.util.Date;

import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * 
 */
public class MedicalDataInstance {
	
	@Override
	public String toString() {
		return "MedicalDataInstance [height=" + height + ", weight=" + weight + ", timestamp=" + timestamp + "]";
	}

	private SimpleFloatProperty height;
	private SimpleFloatProperty weight;
	private SimpleObjectProperty<Date> timestamp;
	
	public MedicalDataInstance(float height, float weight, Date timestamp) {
		this.height = new SimpleFloatProperty(height);
		this.weight = new SimpleFloatProperty(weight);
		this.timestamp = new SimpleObjectProperty<Date>(timestamp);
	}
	
	// Getters 
    public float getHeight() {
        return height.get();
    }

    public SimpleFloatProperty heightProperty() {
        return height;
    }

    public float getWeight() {
        return weight.get(); 
    }

    public SimpleFloatProperty weightProperty() {
        return weight;
    }

    public Date getTimestamp() {
        return timestamp.get();
    }
    
    public SimpleObjectProperty<Date> timestampProperty() {
        return timestamp;
    }


}
