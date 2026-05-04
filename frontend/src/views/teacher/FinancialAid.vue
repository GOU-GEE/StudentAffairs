<template>
  <div class="h-full flex flex-col">
    <teleport to="#header-actions">
      <button class="bg-primary text-on-primary-fixed hover:bg-primary-fixed transition-colors rounded-md px-3.5 py-1.5 text-[0.8125rem] font-semibold flex items-center gap-1 shadow-md">
        <el-icon :size="14"><Plus /></el-icon>发布新岗位/奖助
      </button>
    </teleport>

    <!-- Main Content -->
    <div class="flex-1 bg-surface-container-lowest rounded-xl border border-outline-variant/15 shadow-[0_4px_12px_rgba(25,28,30,0.04)] p-6">
      <el-tabs v-model="activeTab" class="custom-tabs">
        <el-tab-pane label="勤工助学岗位 (防超发并发控制)" name="work-study">
          <div class="flex justify-between items-center mb-4">
            <div class="relative w-80">
              <el-icon class="absolute left-3 top-2.5 text-outline"><Search /></el-icon>
              <input type="text" placeholder="搜索岗位名称..." class="w-full bg-surface border border-outline-variant/30 rounded-lg pl-10 pr-4 py-2 text-sm focus:ring-1 focus:ring-primary focus:border-primary outline-none">
            </div>
            <div class="flex gap-2">
              <span class="text-xs text-secondary bg-surface-container-low px-3 py-1.5 rounded-lg border border-outline-variant/20 flex items-center gap-1">
                <el-icon class="text-error"><Warning /></el-icon>
                并发保护已开启 (乐观锁)
              </span>
            </div>
          </div>

          <el-table :data="tableData" style="width: 100%" header-cell-class-name="bg-surface-container text-secondary font-bold uppercase tracking-wider text-[0.6875rem]">
            <el-table-column prop="title" label="岗位名称" min-width="150" />
            <el-table-column prop="department" label="用人部门" width="120" />
            <el-table-column label="招聘进度" width="180">
              <template #default="{ row }">
                <div class="flex items-center gap-2">
                  <div class="flex-1 bg-surface-container-high h-2 rounded-full overflow-hidden">
                    <div class="bg-primary h-full rounded-full" :style="`width: ${(row.current / row.quota) * 100}%`"></div>
                  </div>
                  <span class="text-xs font-bold">{{ row.current }} / {{ row.quota }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <span v-if="row.current >= row.quota" class="text-[0.6875rem] font-bold px-2 py-1 rounded bg-surface-variant text-secondary border border-outline-variant/30">已满额</span>
                <span v-else class="text-[0.6875rem] font-bold px-2 py-1 rounded bg-green-100 text-green-700 border border-green-200">招募中</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="120" fixed="right">
              <template #default="scope">
                <button class="text-primary hover:underline text-xs font-semibold mr-3">审核名单</button>
              </template>
            </el-table-column>
          </el-table>
          
          <div class="mt-4 flex justify-end">
            <el-pagination background layout="prev, pager, next" :total="50" />
          </div>
        </el-tab-pane>

        <el-tab-pane label="奖学金审核" name="scholarship">
          <div class="py-10 text-center text-secondary">
            <el-icon :size="48" class="mb-2 opacity-50"><Trophy /></el-icon>
            <p>奖学金审核模块开发中...</p>
          </div>
        </el-tab-pane>

        <el-tab-pane label="困难认定" name="poverty">
          <div class="py-10 text-center text-secondary">
            <el-icon :size="48" class="mb-2 opacity-50"><WarningFilled /></el-icon>
            <p>困难认定模块开发中...</p>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import { Plus, Search, Warning, Trophy, WarningFilled } from '@element-plus/icons-vue'
import { ref, onMounted } from 'vue'
import axios from 'axios'

const activeTab = ref('work-study')
const tableData = ref([])

onMounted(async () => {
  try {
    const res = await axios.get('http://localhost:8080/api/financial-aid/jobs')
    if (res.data.code === 200) {
      tableData.value = res.data.data
    }
  } catch (e) {
    console.error('获取岗位失败，请确保后端服务已启动', e)
  }
})
</script>

<style scoped>
/* 针对 Element Plus Tabs 的深度定制以契合 Bento 风格 */
:deep(.el-tabs__nav-wrap::after) {
  background-color: var(--color-outline-variant);
  opacity: 0.3;
}
:deep(.el-tabs__item) {
  color: var(--color-secondary);
  font-weight: 600;
}
:deep(.el-tabs__item.is-active) {
  color: var(--color-primary);
}
:deep(.el-tabs__active-bar) {
  background-color: var(--color-primary);
}
</style>
