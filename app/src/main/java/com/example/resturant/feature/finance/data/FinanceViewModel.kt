package com.example.resturant.feature.finance

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.resturant.feature.finance.data.FinanceDatabase
import com.example.resturant.feature.finance.data.FinanceRecord
import com.example.resturant.feature.finance.data.Partner
import com.example.resturant.feature.finance.data.PartnerDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class FinanceViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = FinanceDatabase.getInstance(application).financeDao()
    private val partnerDao = PartnerDatabase.getInstance(application).partnerDao()

    val partners: Flow<List<Partner>> = partnerDao.getAll()

    fun recordsForSection(section: Int): Flow<List<FinanceRecord>> =
        dao.getBySection(section)

    fun save(record: FinanceRecord) = viewModelScope.launch {
        if (record.id == 0L) dao.insert(record) else dao.update(record)
    }

    fun delete(record: FinanceRecord) = viewModelScope.launch {
        dao.delete(record)
    }

    fun addPartner(name: String) = viewModelScope.launch {
        partnerDao.insert(Partner(name = name))
    }

    fun deletePartner(partner: Partner) = viewModelScope.launch {
        partnerDao.delete(partner)
    }
}