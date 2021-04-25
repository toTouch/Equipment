package com.xiliulou.afterserver.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.entity.SysAreaCodeEntity;
import com.xiliulou.afterserver.mapper.CityMapper;
import com.xiliulou.afterserver.service.CityService;
import com.xiliulou.afterserver.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * (City)表服务实现类
 *
 * @author makejava
 * @since 2021-01-21 18:05:43
 */
@Service("cityService")
@Slf4j
public class CityServiceImpl  extends ServiceImpl<CityMapper, SysAreaCodeEntity> implements CityService {
	@Resource
	private CityMapper cityMapper;


	@Override
	public R cityTree() {
		List<SysAreaCodeEntity> sysAreaCodeEntities = treeList();
		return R.ok(sysAreaCodeEntities);
	}


	public List<SysAreaCodeEntity> treeList() {

		List<SysAreaCodeEntity> list = this.baseMapper.selectList(null);
		List<SysAreaCodeEntity> collect = list.stream().filter(item -> item.getProvincialCode() .equals("-1"))
				.map(item -> {
					item.setChildren(getChrlidens(item, list));
					return item;
				}).collect(Collectors.toList());
		return collect;
	}

	private List<SysAreaCodeEntity> getChrlidens(SysAreaCodeEntity root, List<SysAreaCodeEntity> allList) {
		List<SysAreaCodeEntity> categoryTree = allList.stream().filter(item -> item.getSuperiorCode().equals(root.getCode()))
				.map(item -> {
					item.setChildren(getChrlidens(item, allList));
					return item;
				}).collect(Collectors.toList());
		return categoryTree;
	}

}