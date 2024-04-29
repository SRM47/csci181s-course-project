/**
 * 
 */
package org.healthhaven.model;

import java.time.LocalDate;
import java.util.Date;

import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * 
 */
public class MedicalDataInstance {
	
	@Override
	public String toString() {
		return "MedicalDataInstance [height=" + height + ", weight=" + weight + ", timestamp=" + timestamp + ", identifier=" + identifier + "]";
	}

	private SimpleFloatProperty height;
	private SimpleFloatProperty weight;
	private SimpleObjectProperty<Date> timestamp;
	private SimpleStringProperty identifier;
	
	public MedicalDataInstance(float height, float weight, Date timestamp, String identifier) {
		this.height = new SimpleFloatProperty(height);
		this.weight = new SimpleFloatProperty(weight);
		this.timestamp = new SimpleObjectProperty<Date>(timestamp);
		this.identifier = new SimpleStringProperty(identifier);
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
    
    public String getIdentifier() {
        return identifier.get();
    }

    public SimpleStringProperty identifierProperty() {
        return identifier;
    }


}
