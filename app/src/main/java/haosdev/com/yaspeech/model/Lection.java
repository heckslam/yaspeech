package haosdev.com.yaspeech.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Model of lectures
 */

public class Lection implements Parcelable {
	private String text;
	private String name;
	private String dataLec;
	private String lector;


	public Lection() {
	}

	public Lection(String text, String name, String dataLec, String lector) {
		this.text = text;
		this.name = name;
		this.dataLec = dataLec;
		this.lector = lector;
	}

	public String getDataLec() {
		return dataLec;
	}

	public void setDataLec(String dataLec) {
		this.dataLec = dataLec;
	}

	public String getLector() {
		return lector;
	}

	public void setLector(String lector) {
		this.lector = lector;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	protected Lection(Parcel in) {
		text = in.readString();
		name = in.readString();
		dataLec = in.readString();
		lector = in.readString();
	}

	@Override
	public String toString() {
		return "Lection{" +
				"text='" + text + '\'' +
				", name='" + name + '\'' +
				", dataLec='" + dataLec + '\'' +
				", lector='" + lector + '\'' +
				'}';
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(text);
		dest.writeString(name);
		dest.writeString(dataLec);
		dest.writeString(lector);
	}

	@SuppressWarnings("unused")
	public static final Parcelable.Creator<Lection> CREATOR = new Parcelable.Creator<Lection>() {
		@Override
		public Lection createFromParcel(Parcel in) {
			return new Lection(in);
		}

		@Override
		public Lection[] newArray(int size) {
			return new Lection[size];
		}
	};
}
