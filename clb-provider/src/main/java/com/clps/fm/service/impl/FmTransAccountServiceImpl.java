package com.clps.fm.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.core.sys.util.QueryListUtilForPo;
import com.clps.core.sys.vo.PagePo;
import com.clps.cp.pojo.CpProdparm;
import com.clps.cp.service.ProductParamService;
import com.clps.fm.pojo.FmAcctDtlPo;
import com.clps.fm.pojo.FmItemDtlPo;
import com.clps.fm.pojo.FmLgdProcPo;
import com.clps.fm.service.FmActDtlAddService;
import com.clps.fm.service.FmAcctEntryINQService;
import com.clps.fm.service.FmItemINQService;
import com.clps.fm.service.FmItemdJurService;
import com.clps.fm.service.FmTransAccountingService;
import com.clps.fm.service.FmitemUpdService;
import com.clps.gb.service.TxnJourGenService;

@SuppressWarnings("unused")
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class FmTransAccountServiceImpl extends BaseService implements FmTransAccountingService{
//	@Autowired
//	private PagePo page;
//	@Autowired
//	private FmAcctDtlPo fmactdtl;
//	@Autowired
//	private CpProdparm cpProd;
	@Autowired
	private FmLgdProcPo fmlgitm;
//	@Autowired
//	private FmItemDtlPo fmitemdtl;

	// 错误码
	private static final String ERR_NORMAL = "0000";
	private static final String ERR_CREATFAILURE = "1001";
	private static final String ERR_READFAILURE = "2001";
	private static final String ERR_ADDFAILURE = "3001";
	private static final String ERR_UPDFAILURE = "4001";
	// The returning map
	Map<String, Object> returnMap = new HashMap<>();
	
	/**
     * FM - 交易记账
     * @return 成功or失败
     * @throws Exception
     */
	@Reference(version = "1.0.0")
		private FmAcctEntryINQService service2;
	@Reference(version = "1.0.0")
		private TxnJourGenService service1;
	@Reference(version = "1.0.0")
		private ProductParamService service3;
	@Reference(version = "1.0.0")
		private FmActDtlAddService service4;
	@Reference(version = "1.0.0")
		private FmItemINQService service5;
	@Reference(version = "1.0.0")
		private FmitemUpdService service6;
	@Reference(version = "1.0.0")
		private FmItemdJurService service7;
  
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
			public Map<String, Object> TransAccounting(Map<String, Object> map) throws Exception {
			
		        PagePo page = new PagePo();
//		        FmLgdProcPo fmlgitm = new FmLgdProcPo();
		        FmAcctDtlPo fmactdtl =new FmAcctDtlPo();
		        CpProdparm cpProd = new CpProdparm();
		        FmItemDtlPo fmitemdtl = new FmItemDtlPo();
    			Map<String, Object> map1 = new HashMap<String, Object>();
    			Map<String, Object> map2 = new HashMap<String, Object>();
//    			Map<String, Object> map3 = new HashMap<String, Object>();
//    			Map<String, Object> map4 = new HashMap<String, Object>();
//    			Map<String, Object> map5 = new HashMap<String, Object>();
//    			Map<String, Object> map6 = new HashMap<String, Object>();
//    			Map<String, Object> map7 = new HashMap<String, Object>();
    			Map<String, Object> mapa = new HashMap<String, Object>();
    			Map<String, Object> mapb= new HashMap<String,Object>();
    			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    			List<Map<String, Object>> list1 = new ArrayList<Map<String, Object>>();
    			List<Map<String, Object>> list2 = new ArrayList<Map<String, Object>>();
    			// 生成流水号
    			map2.put("create_user", "sys_user");
				map2.put("update_user", "sys_user");
				map2.put("initial","00000000000000");
				map2.put("length","19");
			    map2 =  service1.txnJourGen(map2);
			    if (map2==null){
			    	returnMap.put("resp_code", ERR_CREATFAILURE);
			    	return returnMap;
			    }
			    map.put("jour_nbr", map2.get("jour_nbr"));
//			    fmtxnact.setTran_jour(map2.get("jour_nbr").toString());
			 
			    
			 // 记账分录查询
			   
				mapb.put("tran_id",map.get("tran_id"));
				mapb.put("cond_seq",map.get("cond_seq"));
				page.setSearch(mapb.toString());
				mapb.put("test","");
				map1.put("search", mapb);
				map1.put("limit", 10);
			    map1.put("page",1);
			    map1 = QueryListUtilForPo.changeInputData(page);
			    map1 =  service2.fmQueryOneService(map1);
			    if (map1==null){
			    	returnMap.put("resp_code", ERR_READFAILURE);
			    	return returnMap;
			    }
//			    for (Entry<String, Object> entry : map1.entrySet()) {
//			    	if (entry.getKey().startsWith("list")) {
//			    		Map mapd=new HashMap<String,Object>();
//						mapd= (Map) entry.getValue();
//						list.add(mapd);
//			    	}
			    	list = (List<Map<String, Object>>) map1.get("list");
//			    }
// 			    mapd.put("list_01", list.subList(0,1));
//			    mapd.put("list_02", list.subList(1,2));-
//			    List<List<Object>> list1 = Arrays.asList(list);
// 			    mapc.put("listdata", list);			    
//			    for(Entry<String, Object> m : mapd.entrySet())
			    	list2 = (List<Map<String, Object>>) map.get("list");
			    for(int i=0;i<list.size();i++){    
			    	Map mapc =  list.get(i);
//			    	Iterator iterator = map.keySet().iterator();
//			    	 while (iterator.hasNext())
			    	 for(int j=0;j<list2.size();j++) { 
			    		 	Map mapd = list2.get(j);
			    		 	fmactdtl.setTran_id(map.get("tran_id").toString());
			    		 	fmactdtl.setCond_seq(map.get("cond_seq").toString());
			    		 	fmactdtl.setTran_date(DateTimeUtils.changeToDate());
			    		 	fmactdtl.setTran_time(DateTimeUtils.changeToTime());
			    		 	fmactdtl.setTran_amt(mapd.get("tran_amt").toString());
//					    	map3.put("tran_id", map.get("tran_id"));
//					    	map3.put("cond_seq", map.get("cond_seq"));
//					    	map3.put("tran_date", DateTimeUtils.changeToDate());
//					    	map3.put("tran_time", DateTimeUtils.changeToTime());
//					    	map3.put("tran_amt", map.get("tran_amt1"));
//					    	String dc_flag=(String) iterator.next();
					    	fmactdtl.setDc_flag(mapc.get("dc_flag").toString());
//					    	map3.put("dc_flag", mapc.get("dc_flag"));					    		
//					    	String acct_org=(String) iterator.next();
					    	fmactdtl.setAcct_org(mapd.get("acct_org").toString());
//					    	map3.put("acct_org", mapc.get("acct_org"));					    		
//					    	String item_seq=(String) iterator.next();
					    	fmactdtl.setItem_seq(mapc.get("item_seq").toString());
//					    	map3.put("item_seq", mapc.get("item_seq"));					    							  				    		
//					    	String acct_item=(String) iterator.next();					    		
					    	String a = mapc.get("acct_item").toString().substring(0,2);					    		
					    	String b = mapc.get("acct_item").toString().substring(2,4);

// 					    	list1 = (List<Map<String, Object>>) map1.get("list");
// 					    	mapa.get(list1);
//					    	for (Map.Entry<String, Object> entry :  mapa.entrySet()) {
					    				
					    		if (mapd.get("sub_cod")==b||a.equals("00")) {
//					    			if ((a.equals("00"))||(entry.getValue().equals(b))){
					    				
//					    				map4.put("acct_prod", map.get("acct_prod"));
					    				cpProd.setProduct_id(mapd.get("acct_prod").toString());
					    				cpProd=service3.queryProductParam(cpProd);//产品参数查询
					    				if (cpProd==null){
//							    			returnMap = MapAndObjectUtils.copyPropertiesToMap(cpProd, returnMap);
									    	returnMap.put("resp_code", ERR_READFAILURE);
									    	return returnMap;
									    }
					    				fmactdtl.setAcct_item(cpProd.getSubject_number().substring(11,19));
//					    				map3.put("acct_item", map4.get("subject_number").toString().substring(11,19));					    			
					    			}
										          		
					    			else
					    				{		
					    				fmactdtl.setAcct_item(mapc.get("acct_item").toString());
//					    				map3.put("acct_item", mapc.get("acct_item"));					    			
										}
//					    		}	 					    					
//					    	}	
					    								    								    		
//					    		
//					    		for(int j=0;j<2;j++){    
//								    Map mape =  list1.get(j);
//								    Iterator iterator1 = mape.keySet().iterator();
//      								    while (iterator1.hasNext()){								    	
//					    			}	 					    			
					    		/*if (map1.get("tran_amt") !=null || (map.get("amt_pionter")==map1.get("tran_amt"))){
					    			map3.put("tran_amt", map.get("amt_pionter"));			    			
					    		}*/
					    		
					    		String item_key;
					    		String item_key1="00000000000";					    		
					    		String item_key2="156";
					    		String item_key3="CNY";
					    		String item_key4= fmactdtl.getAcct_item();				    		
					    		String item_key5="000000";
					    		item_key=item_key1+item_key2+item_key3+item_key4+item_key5;
					    		
//					    		map5.put("item_key",item_key);
					    		fmlgitm.setItem_key(item_key);
					    		fmactdtl.setTran_jour(map2.get("jour_nbr").toString());
//					    		map3.put("tran_jour", map2.get("jour_nbr"));
					    		if (fmactdtl.getDc_flag().equals("D")){
					    			fmactdtl.setTran_seq("1");				    			
					    		}
					    		else{
					    			fmactdtl.setTran_seq("2");
					    		}
					    		fmactdtl=service4.insertAcctDetail(fmactdtl);//记账分录明细添加
					    		if (fmactdtl==null){
//					    			returnMap = MapAndObjectUtils.copyPropertiesToMap(fmactdtl, returnMap);
							    	returnMap.put("resp_code", ERR_ADDFAILURE);
							    	return returnMap;
							    }
					    		fmlgitm=service5.fmQueryOneService(fmlgitm);
					    		if (fmlgitm==null){
//					    			returnMap = MapAndObjectUtils.copyPropertiesToMap(fmlgitm, returnMap);
							    	returnMap.put("resp_code", ERR_READFAILURE);
							    	return returnMap;
							    }
//					    		map6.put("item_key", map5.get("item_key"));
//					    		map6.put("op_time_stamp", map5.get("op_time_stamp"));
//					    		map6.put("buss_type", map5.get("buss_type"));
//					    		map6.put("item_name", map5.get("item_name"));
//					    		map6.put("afc_bal", map5.get("afc_bal"));
					    		BigDecimal b1 = new BigDecimal(fmlgitm.getFace_bal());  
			    				BigDecimal b2 = new BigDecimal(mapd.get("tran_amt").toString());
			    				BigDecimal b3 = new BigDecimal(fmlgitm.getSecond_bal());
			    				BigDecimal b4 = new BigDecimal(fmlgitm.getAfc_bal());
					    		if (map1.get("dc_flag").equals("D")){
					    			if (fmlgitm.getBal_dire_flg().equals("0")){
//					    				Double bal=Double.parseDouble((String) map5.get("face_bal"))+Double.parseDouble((String) map.get("tran_amt"));
//					    				bal = Double.parseDouble((String) map5.get("face_bal"));
					    				b1=b1.add(b2);
					    				b4=b1;
					    				b3=b3.add(b2);
					    			}
					    			if (fmlgitm.getBal_dire_flg().equals("1")){
					    				b1=b2.subtract(b1);
					    				b4=b1;
					    				b3=b2.subtract(b3);				
					    			}
					    			if ((fmlgitm.getBal_dire_flg().equals("2"))&&(Double.valueOf(fmlgitm.getAfc_bal()))>0){
					    				b1=b1.add(b2);
					    				b4=b1;
					    				b3=b3.add(b2);
					    			}
					    			if ((fmlgitm.getBal_dire_flg().equals("2"))&&(Double.valueOf(fmlgitm.getAfc_bal()))<0){
					    				b1=b2.subtract(b1);
					    				b4=b1;
					    				b3=b2.subtract(b3);
					    			}
					    		}
					    		if (map1.get("dc_flag") !="D") {
					    			if (fmlgitm.getBal_dire_flg().equals("0")){
					    				b1=b2.subtract(b1);
					    				b4=b1;
					    				b3=b2.subtract(b3);	
					    			}
					    			if (fmlgitm.getBal_dire_flg().equals("1")){
					    				b1=b1.add(b2);
					    				b4=b1;
					    				b3=b3.add(b2);
					    			}
					    			if ((fmlgitm.getBal_dire_flg().equals("2"))&&(Double.valueOf(fmlgitm.getAfc_bal()))>0){
					    				b1=b2.subtract(b1);
					    				b4=b1;
					    				b3=b2.subtract(b3);
					    			}
					    			if ((fmlgitm.getBal_dire_flg().equals("2"))&&(Double.valueOf(fmlgitm.getAfc_bal()))<0){
					    				b1=b1.add(b2);
					    				b4=b1;
					    				b3=b3.add(b2);
					    			}
					    		}
					    		
//					    		map6.put("bal_dire_flg", map5.get("bal_dire_flg"));
					    		BigDecimal a1 = new BigDecimal(fmlgitm.getCrnt_day_dr_amt());  
			    				BigDecimal a2 = new BigDecimal(mapd.get("tran_amt").toString());
			    				BigDecimal a3 = new BigDecimal(fmlgitm.getCrnt_day_dr_qty());
			    				BigDecimal a4 = new BigDecimal(fmlgitm.getMnaccum_dr_amt());
			    				BigDecimal a5 = new BigDecimal(fmlgitm.getMnaccum_dr_item());
			    				BigDecimal a6 = new BigDecimal(fmlgitm.getMnaccum_dr_bal());
			    				BigDecimal a7 = new BigDecimal(fmlgitm.getSsn_dr_totl_amt());
			    				BigDecimal a8 = new BigDecimal(fmlgitm.getSsn_dr_totl_number());
			    				BigDecimal a9 = new BigDecimal(fmlgitm.getSsn_dr_totl_bal());
			    				BigDecimal a10 = new BigDecimal(fmlgitm.getYr_accum_dr_amt());
			    				BigDecimal a11 = new BigDecimal(fmlgitm.getYr_accum_dr_qty());
			    				BigDecimal a12 = new BigDecimal(fmlgitm.getYr_accum_dr_bal());
					    		
					    		BigDecimal c1 = new BigDecimal(fmlgitm.getCrnt_day_cr_amt());  
			    				BigDecimal c2 = new BigDecimal(mapd.get("tran_amt").toString());
			    				BigDecimal c3 = new BigDecimal(fmlgitm.getCrnt_day_cr_qty());
			    				BigDecimal c4 = new BigDecimal(fmlgitm.getMnaccum_cr_amt());
			    				BigDecimal c5 = new BigDecimal(fmlgitm.getMnaccum_cr_item());
			    				BigDecimal c6 = new BigDecimal(fmlgitm.getMnaccum_cr_bal());
			    				BigDecimal c7 = new BigDecimal(fmlgitm.getSsn_cr_totl_amt());
			    				BigDecimal c8 = new BigDecimal(fmlgitm.getSsn_cr_totl_number());
			    				BigDecimal c9 = new BigDecimal(fmlgitm.getSsn_cr_totl_bal());
			    				BigDecimal c10 = new BigDecimal(fmlgitm.getYr_accum_cr_amt());
			    				BigDecimal c11 = new BigDecimal(fmlgitm.getYr_accum_cr_qty());
			    				BigDecimal c12 = new BigDecimal(fmlgitm.getYr_accum_cr_bal());
					    		if (!map1.get("dc_flag").equals("D")){					    			
				    				a1=a1.add(a2);
				    				a3=a3.add(new BigDecimal(1));
				    				a4=a4.add(a2);
				    				a5=a5.add(new BigDecimal(1));
				    				a6=a6.add(a2);
				    				a7=a7.add(a2);
				    				a8=a8.add(new BigDecimal(1));
				    				a9=a9.add(a2);
				    				a10=a10.add(a2);
				    				a11=a11.add(new BigDecimal(1));
				    				a12=a12.add(a2);  
				    				
					    		}
					    		if (!map1.get("dc_flag").equals("C")){	    				
				    				c1=c1.add(c2);
				    				c3=c3.add(new BigDecimal(1));
				    				c4=c4.add(c2);
				    				c5=a5.add(new BigDecimal(1));
				    				c6=a6.add(c2);
				    				c7=a7.add(c2);
				    				c8=a8.add(new BigDecimal(1));
				    				c9=a9.add(c2);
				    				c10=a10.add(c2);
				    				c11=a11.add(new BigDecimal(1));
				    				c12=a12.add(c2);
					    		}
					    		
//					    		map6.put("face_bal", b1);
					    		fmlgitm.setFace_bal(b1.toString());
//					    		map6.put("second_bal", b3);
					    		fmlgitm.setSecond_bal(b3.toString());
//					    		map6.put("afc_bal", b4);
					    		fmlgitm.setAfc_bal(b4.toString());
//					    		map6.put("crnt_day_dr_amt", a1);
					    		fmlgitm.setCrnt_day_dr_amt(a1.toString());
//					    		map6.put("crnt_day_cr_amt", c1);
					    		fmlgitm.setCrnt_day_cr_qty(c1.toString());
//					    		map6.put("crnt_day_dr_qty", a3);
					    		fmlgitm.setCrnt_day_dr_qty(a3.toString());
//					    		map6.put("crnt_day_cr_qty", c3);
					    		fmlgitm.setCrnt_day_cr_qty(c3.toString());
//					    		map6.put("mnaccum_dr_amt", a4);
					    		fmlgitm.setMnaccum_dr_amt(a4.toString());
//					    		map6.put("mnaccum_cr_amt", c4);
					    		fmlgitm.setMnaccum_cr_amt(c4.toString());
//					    		map6.put("mnaccum_dr_item", a5);
					    		fmlgitm.setMnaccum_dr_item(a5.toString());
//					    		map6.put("mnaccum_cr_item", c5);
					    		fmlgitm.setMnaccum_cr_item(c5.toString());
//					    		map6.put("mnaccum_dr_bal", a6);
					    		fmlgitm.setMnaccum_dr_bal(a6.toString());
//					    		map6.put("mnaccum_cr_bal", c6);
					    		fmlgitm.setMnaccum_cr_bal(c6.toString());
//					    		map6.put("ssn_dr_totl_amt", a7);
					    		fmlgitm.setSsn_dr_totl_amt(a7.toString());
//					    		map6.put("ssn_cr_totl_amt", c7);
					    		fmlgitm.setSsn_cr_totl_amt(c7.toString());
//					    		map6.put("ssn_dr_totl_number", a8);
					    		fmlgitm.setSsn_dr_totl_number(a8.toString());
//					    		map6.put("ssn_cr_totl_number", c8);
					    		fmlgitm.setSsn_cr_totl_number(c8.toString());
//					    		map6.put("ssn_dr_totl_bal", a9);
					    		fmlgitm.setSsn_dr_totl_bal(a9.toString());
//					    		map6.put("ssn_cr_totl_bal", c9);
					    		fmlgitm.setSsn_cr_totl_bal(c9.toString());
//					    		map6.put("yr_accum_dr_amt", a10);
					    		fmlgitm.setYr_accum_dr_amt(a10.toString());
//					    		map6.put("yr_accum_cr_amt", c10);
					    		fmlgitm.setYr_accum_cr_amt(c10.toString());
//					    		map6.put("yr_accum_dr_qty", a11);
					    		fmlgitm.setYr_accum_dr_qty(a11.toString());
//					    		map6.put("yr_accum_cr_qty", c11);f 
					    		fmlgitm.setYr_accum_cr_qty(c11.toString());
//					    		map6.put("yr_accum_dr_bal", a12);
					    		fmlgitm.setYr_accum_dr_bal(a12.toString());
//					    		map6.put("yr_accum_cr_bal", c12);
					    		fmlgitm.setYr_accum_cr_bal(c12.toString());
					    		
//					    		map6.put("dr_flt_intr", map5.get("dr_flt_intr"));
//					    		map6.put("cr_flt_intr", map5.get("cr_flt_intr"));
//					    		map6.put("tprd_acc_bal", map5.get("tprd_acc_bal"));
//					    		map6.put("crnt_prd_cd", map5.get("crnt_prd_cd"));
//					    		map6.put("acct_largst_bal", map5.get("acct_largst_bal"));
//					    		map6.put("over_draw_lagst_amt", map5.get("over_draw_lagst_amt"));
//					    		map6.put("over_draw_start_dt", map5.get("over_draw_start_dt"));
//					    		map6.put("last_acc_int_dt", map5.get("last_acc_int_dt"));
//					    		map6.put("last_tsf_accbal_dt", map5.get("last_tsf_accbal_dt"));
//					    		map6.put("last_acc_dt", map5.get("last_acc_dt"));
//					    		map6.put("opac_dt", map5.get("opac_dt"));
//					    		map6.put("close_dt", map5.get("close_dt"));
//					    		map6.put("frz_sts", map5.get("frz_sts"));
//					    		map6.put("acct_sts", map5.get("acct_sts"));
//					    		map6.put("intc_flg", map5.get("intc_flg"));
//					    		map6.put("intc_cycle", map5.get("intc_cycle"));
//					    		map6.put("pay_int_acct_no", map5.get("pay_int_acct_no"));
//					    		map6.put("cola_int_acct_no", map5.get("cola_int_acct_no"));
//					    		map6.put("respon_cent", map5.get("respon_cent"));
//					    		map6.put("acct_page_fmt", map5.get("acct_page_fmt"));
//					    		map6.put("acct_mode", map5.get("acct_mode"));
//					    		map6.put("opun_inst", map5.get("opun_inst"));
//					    		map6.put("cust_no", map5.get("cust_no"));
//					    		map6.put("opun_cod", map5.get("opun_cod"));
//					    		map6.put("amndr_no", map5.get("amndr_no"));
//					    		map6.put("second_bill_type", map5.get("second_bill_type"));
//					    		map6.put("second_last_bal", map5.get("second_last_bal"));
//					    		map6.put("lst_prt_rcd_dt", map5.get("lst_prt_rcd_dt"));
//					    		map6.put("max_prnt_txn_no", map5.get("max_prnt_txn_no"));
//					    		map6.put("open_mode", map5.get("open_mode"));
//					    		map6.put("intr_rcrd_flg", map5.get("intr_rcrd_flg"));
//					    		map6.put("oppo_acct_no", map5.get("oppo_acct_no"));
//					    		map6.put("auto_invst_intr_cod", map5.get("auto_invst_intr_cod"));
//					    		map6.put("auto_invst_flt_intr", map5.get("auto_invst_flt_intr"));
//					    		map6.put("cost_cntr", map5.get("cost_cntr"));
//					    		map6.put("due_dt", map5.get("due_dt"));
//					    		map6.put("dlay_flg", map5.get("dlay_flg"));
//					    		map6.put("over_draw_accum", map5.get("over_draw_accum"));
//					    		map6.put("extn_sts", map5.get("extn_sts"));
//					    		map6.put("dt_bgst_seq_no_n", map5.get("dt_bgst_seq_no_n"));
//					    		map6.put("last_bal", map5.get("last_bal"));
//					    		map6.put("second_bill_dr_amt", map5.get("second_bill_dr_amt"));
//					    		map6.put("second_bill_cr_amt", map5.get("second_bill_cr_amt"));
//					    		map6.put("acct_max_bal", map5.get("acct_max_bal"));
//					    		map6.put("opac_instn", map5.get("opac_instn"));
					    		int re=service6.editOneService(fmlgitm);
					    		if (re!=1){
//					    			returnMap = MapAndObjectUtils.copyPropertiesToMap(fmlgitm, returnMap);
							    	returnMap.put("resp_code", ERR_UPDFAILURE);
							    	return returnMap;
							    }
					    		String txn_jour1="00000000";
					    		String txn_jour2=map.get("tran_jour").toString().substring(0, 11);
					    		String txn_jour=txn_jour1+txn_jour2;
					    		fmitemdtl.setTxn_jour_t(txn_jour);
//					    		if (m.getKey()=="txn_jour"){
//					    			fmitemdtl.put("txn_jour".substring(8), m.getValue().toString().substring(0,11));
//					    		}			
					    		
					    		fmitemdtl.setTxn_seq(String.valueOf(i));				    		
					    		fmitemdtl.setDoc_type(map.get("doc_type").toString());				    		
 					    		fmitemdtl.setDoc_nbr(map.get("doc_nbr").toString());				    		
 					    		fmitemdtl.setTxn_descrip(map.get("tran_desp").toString());
					    		fmitemdtl.setTxn_amt(mapd.get("tran_amt").toString());					    		
					    		fmitemdtl.setDc_cod(mapc.get("dc_flag").toString());					    		
 					    		fmitemdtl.setItem_bal(fmlgitm.getFace_bal());				    
 					    		fmitemdtl.setOpr_nbr(map.get("opr_nbr").toString());			    		
 					    		fmitemdtl.setRchr_nbr(map.get("rchr_nbr").toString());					    		
 					    		fmitemdtl.setTxn_instin(map.get("txn_instn").toString());	
 					    		fmitemdtl.setVal_date(map.get("val_date").toString());					    		
					    		fmitemdtl.setTxn_fxr(map.get("txn_fxr").toString());					    		
 					 		    fmitemdtl.setSecond_amt(mapd.get("tran_amt").toString());					    		
 					    		fmitemdtl.setSecond_bal(fmlgitm.getSecond_bal());	    		
 					    		fmitemdtl.setDscrp_cod(map.get("desrp_cod").toString());					    		
 					    		fmitemdtl.setTxn_seq_nbr(map.get("txn_seq_nbr").toString());
 					    		fmitemdtl.setTxn_type(map.get("txn_type").toString());	
 					    		fmitemdtl.setTxn_unit(map.get("txn_unit").toString());
					    		fmitemdtl.setItem_nbr(mapc.get("acct_item").toString());					    		
					    		fmitemdtl=service7.insertService(fmitemdtl);
					    		
					    		}
					    			
 					    	}
			    if (fmitemdtl==null){
			    	returnMap.put("resp_code", ERR_ADDFAILURE);
			    	return returnMap;
	    			
			    }
	    		else{
	    			returnMap.put("resp_code", ERR_NORMAL);
	                return returnMap;			    			    
				
			    }
			}    

}
