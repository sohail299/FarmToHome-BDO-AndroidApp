package com.switchsolutions.farmtohome.bdo.room_db.branch


class BranchRespository(private val branchDao: BranchDAO) {

    val branch = branchDao.getAllBranches()

    suspend fun insert(branchEntityClass: BranchEntityClass): Long {
        return branchDao.insertBranches(branchEntityClass)
    }

    suspend fun update(branchEntityClass: BranchEntityClass): Int {
        return branchDao.updateBranch(branchEntityClass)
    }


    suspend fun deleteAll(): Int {
        return branchDao.deleteAll()
    }

}