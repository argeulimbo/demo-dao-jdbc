package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentDaoJDBC implements DepartmentDao {

	private Connection conn;
	
	public DepartmentDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Department obj) {
		PreparedStatement st = null;
		try { 
			 st = conn.prepareStatement("INSERT INTO coursejdbc.department "
						+ "(Id, Name) "
						+ "VALUES "
						+ "(?, ?)", Statement.RETURN_GENERATED_KEYS		
						);
			st.setInt(1, obj.getId()); 
			st.setString(2, obj.getName());
			int rowsAffected = st.executeUpdate();
			if (rowsAffected > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if (rs.next()) {
					int id = rs.getInt(1);
					obj.setId(id);
				}
				DB.closeResultSet(rs);
			}
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void update(Department obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"UPDATE coursejdbc.department "
					+ " SET id = ?, name = ? "
					+ "	WHERE Id = ?");
			
			st.setInt(1, obj.getId());
			st.setString(2, obj.getName());
			st.executeUpdate();			
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void deleteById(Department obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"DELETE FROM coursejdbc.department "
					+ "WHERE Id = ? "
					);
			st.setInt(1, obj.getId());
			st.executeUpdate();
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
		}		
	}

	@Override
	public Department findById(Integer id) {			
		PreparedStatement st = null;
		ResultSet rs = null;		
		try {
			st = conn.prepareStatement(
					"SELECT * FROM coursejdbc.department "
					+ "WHERE id = ? ");
			
			st.setInt(1, id);
			rs = st.executeQuery();
			if (rs.next()) {
				Department dep = instantiateDepartment(rs);		
				return dep;
			}
			return null;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeStatement(st);
		}
	}

	private Department instantiateDepartment(ResultSet rs) throws SQLException {
		Department dep = new Department();
		dep.setId(rs.getInt("id"));
		dep.setName(rs.getString("name"));
		
		return dep;
	}

	@Override
	public List<Department> findAll(Department department) {
		PreparedStatement st = null;
		ResultSet rs = null;		
		try {
			st = conn.prepareStatement(
					"SELECT * FROM coursejdbc.department "
					+ "WHERE id = ?"
					+ "ORDER BY id");			
			st.setInt(1, department.getId());
			rs = st.executeQuery();
			List<Department> list = new ArrayList<>();
			Map<Integer, Department> map = new HashMap<>();
			
			while (rs.next()) {				
				Department dep = map.get(rs.getInt("id"));				
				if (dep == null) {
					dep = instantiateDepartment(rs);
					map.put(rs.getInt("DepartmentId"), dep);
				}
				list.add(dep);
			}
			return list;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(st);
			DB.closeStatement(st);
		}
	}
}
