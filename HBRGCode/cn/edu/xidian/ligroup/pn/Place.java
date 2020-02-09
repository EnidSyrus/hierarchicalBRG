package cn.edu.xidian.ligroup.pn;

public class Place implements Comparable<Place> {

	private String name;
	private int token;
	
	public Place(String name, int token) {
		this.name = name;
		this.token = token;
	}
	public Place(String name) {
		this(name, 0);
	}
	public Place(Place p) {
		this(p.name, p.token);
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	public void setToken(int token) {
		this.token = token;
	}
	
	public int getToken() {
		return token;
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + token;
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Place other = (Place) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (token != other.token)
			return false;
		return true;
	}
	
	
	
	@Override
	public int compareTo(Place p) {
		return name.compareToIgnoreCase(p.getName());
	}
	@Override
	public String toString() {
		return name+"("+token+")";
	}
}
