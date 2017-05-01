package main;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DbDataSource {
	
	private DbHelper helper;

	public DbDataSource(){
		helper = DbHelper.getInstance();
	}
	
	public List<Contact> selectAll(){
		return select("");
	}
	
	public List<Contact> select(String where){
		List<Contact> data = new ArrayList<Contact>();
        try {
        	Connection connection = helper.getConnection();
            Statement stm= connection.createStatement();
            ResultSet rs = stm.executeQuery("Select * from contact "+where+" order by lastName");
            
            while (rs.next()) {
            	ResultSetMetaData meta = rs.getMetaData();
            	Contact obj = new Contact();
            	for (int i = 1, n = meta.getColumnCount() + 1; i < n; i++) {
            		String methodName = "set" + meta.getColumnName(i).toLowerCase();
            		Method method = null;
            		for (Method mtd : obj.getClass().getDeclaredMethods()) {
						if(mtd.getName().toLowerCase().equals(methodName)){
							method = mtd;
						}
					}
                    method.invoke(obj, rs.getObject(i));            		 
                }
            	data.add(obj);
            }
            rs.close();
            stm.close();
            connection.close();
        } catch (Exception ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            return new ArrayList<Contact>();        
		}
        return data;
	}

	public boolean save(Contact obj) {
		String sql = mountQuery(obj, false);
		return executeSQL(sql);
	}
	
	public boolean edit(Contact obj) {
		String sql = mountQuery(obj, true);
		return executeSQL(sql);
	}
	
	public boolean delete(Contact obj) {
		String sql = "DELETE FROM contact WHERE id = "+obj.getId();
		return executeSQL(sql);
	}

	private boolean executeSQL(String sql) {
		try {
			Connection connection = helper.getConnection();
	        Statement stm= connection.createStatement();
			stm.execute(sql);
            ResultSet keyset = stm.getGeneratedKeys();
            Integer key = 0;
            if ( keyset.next() ) {
                // Retrieve the auto generated key(s).
                key = keyset.getInt(1);
            }
            keyset.close();
            stm.close();
            connection.close();
            return  key != null;
        } catch (SQLException ex) {
        	Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
		return false;
	}

	private String mountQuery(Contact obj, boolean update) {
		StringBuilder query = new StringBuilder();
		if(update){			
			query.append("UPDATE contact set ");
			query.append("firstName = '"+obj.getFirstName()+"', ");
			query.append("lastName = '"+obj.getLastName()+"', ");
			query.append("email = '"+obj.getEmail()+"', ");
			query.append("address = '"+obj.getAddress()+"', ");
			query.append("phone = '"+obj.getPhone()+"', ");
			query.append("companyName = '"+obj.getCompanyName()+"', ");
			query.append("companyPhone = '"+obj.getCompanyPhone()+"', ");
			query.append("notes = '"+obj.getNotes()+"' ");
			query.append(" WHERE id = "+obj.getId());
		}else{
			query.append("INSERT INTO contact ");
			query.append("( firstName, lastName, email, address, phone, companyName, companyPhone, notes )");
			query.append(" VALUES ");
			query.append("('"+obj.getFirstName()+"', '"+obj.getLastName()+"','"+obj.getEmail()+"','"+obj.getAddress()+ 
					"','"+obj.getPhone()+"','"+obj.getCompanyName()+"','"+obj.getCompanyPhone()+"','"+obj.getNotes()+"');");
		}
		return query.toString();
	}

	public List<Contact> get(Integer id) {		
		return select(" WHERE id = "+id);
	}

}
