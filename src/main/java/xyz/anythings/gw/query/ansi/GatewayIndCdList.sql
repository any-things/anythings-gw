select x.ind_cd from (
	select cell_cd, ind_cd
	from cells
	where domain_id = :domainId AND active_flag = :active
) x, (
	select gw_cd, ind_cd
	from indicators
	where domain_id = :domainId
		  and gw_cd = (select gw_cd from tb_gateway where domain_id = :domainId and gw_nm = :gwPath)
) y
where 
	x.ind_cd = y.ind_cd
order by 
	x.cell_cd