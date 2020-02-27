package cn.com.aig.recommend.service;

import cn.com.aig.recommend.entiy.Location;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service(value="locationService")
@Transactional(readOnly = true)
public class LocationService  {

	public List<Location> getLocationBySupLocalCode(String supLocalCode) {
		// TODO Auto-generated method stub
		Location location = new Location();
		location.setSupLocalCode(supLocalCode);
		//return locationtMapper.getList(location);
		return  null;
	}

	public List<Location> getLocationByLvAndFlag(int lv,String flag){
		Location location = Location.builder().lv(lv).flag(flag).build();
//		location.setLv(lv);
//		location.setFlag(flag);
		//return locationtMapper.getList(location);

		return  null;
	}

	public List<Location> findLocationTreeNodeByLocalCode(String localCode){
		return getLocationBySupLocalCode(localCode);
	}



	public List<Location> getLocationListByLevel(Integer lv){
		Location location = new Location();
		location.setLv(lv);
		//return locationtMapper.getList(location);

		return  null;
	}

	public List<Location> getLocationListById(Long id){
		Location location = new Location();
		location.setId(id);
		//return locationtMapper.getList(location);

		return  null;
	}
}
