package com.joinway.mobile.service;

import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.joinway.bean.exception.DuplicateDataException;
import com.joinway.bean.exception.ValidationException;
import com.joinway.db.constant.DBValueConstants;
import com.joinway.db.repository.TableRepository;
import com.joinway.mobile.bean.domain.LoginUser;
import com.joinway.mobile.bean.domain.User;
import com.joinway.mobile.bean.form.LoginForm;
import com.joinway.mobile.bean.form.LogoutForm;
import com.joinway.mobile.bean.form.RegisterForm;
import com.joinway.mobile.bean.view.LoginView;
import com.joinway.mobile.bean.view.LogoutView;
import com.joinway.mobile.repository.MobileRepository;
import com.joinway.utils.CipherUtils;

@Service
public class MobileService {

	@Autowired MobileRepository appRepository;
	
	@Autowired TableRepository tableRepository;
	
	@Transactional(rollbackFor=Throwable.class)
	public LoginView register(RegisterForm form) throws Exception {
		LoginUser loginUser = appRepository.findLoginUser(form.getName());
		if(loginUser != null){
			throw new DuplicateDataException("user has already registered");
		}
		
		/*
		 * 保存用户注册信息
		 */
		Date today = Calendar.getInstance().getTime();
		
		loginUser = new LoginUser();
		loginUser.setLoginName(form.getName());
		loginUser.setPassword(CipherUtils.encrypt(form.getPassword()));
		loginUser.setLoginCount(0);
		loginUser.setCreateTime(today);
		
		tableRepository.save(loginUser);
		
		/*
		 * 保存用户信息
		 */
		User user = new User();
		user.setId(loginUser.getId());
		user.setCellPhone(form.getCellPhone());
		user.setName(form.getName());
		tableRepository.insert(user);
		
		LoginView view = new LoginView();
		
		view.setUserId(loginUser.getId());
		
		return view;
	}
	
	@Transactional(rollbackFor=Throwable.class)
	public LoginView login(LoginForm form) throws Exception {
		LoginUser loginUser = appRepository.findLoginUser(form.getName(), CipherUtils.encrypt(form.getPassword()));
		if(loginUser == null){
			throw new ValidationException("user doesn't exist");
		}
		
		/*
		 * 更新用户登录信息
		 */
		int count = loginUser.getLoginCount();
		Date today = Calendar.getInstance().getTime();
		
		loginUser.setLoginCount(count + 1);
		loginUser.setLastLoginTime(today);
		loginUser.setLoginStatus(DBValueConstants.YES);
		tableRepository.save(loginUser);
		
		User user = tableRepository.find(loginUser.getId(), User.class);
		user.setImId(form.getImId());
		user.setCellPhoneType(form.getMobileType());
		
		tableRepository.save(user);
		
		LoginView view = new LoginView();
		
		view.setUserId(loginUser.getId());
		
		return view;
	}
	
	@Transactional(rollbackFor=Throwable.class)
	public LogoutView logout(LogoutForm form) throws Exception{
		LoginUser loginUser = tableRepository.find(form.getUserId(), LoginUser.class);
		
		if(loginUser == null){
			throw new ValidationException("user doesn't exist");
		}
		
		/*
		 * 保存用户登出信息
		 */
		loginUser.setLoginStatus(DBValueConstants.NO);
		tableRepository.save(loginUser);
		
		return new LogoutView();
	}
}

