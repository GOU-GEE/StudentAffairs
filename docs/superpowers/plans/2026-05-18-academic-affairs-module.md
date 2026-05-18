# 教务端模块接通 — 实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 将教务端5个全mock页面接通真实后端API，新增Course和MidTermAppraisal实体及配套接口，新增辅导员端中期鉴定收集页面。

**Architecture:** 分离式Controller — AcademicController负责预警+成绩CRUD+统计，CourseController负责课程CRUD，MidTermController负责中期鉴定提交与查询。前端使用axios直接调用localhost:8080，与项目现有模式一致。

**Tech Stack:** Spring Boot 3 + JPA (Backend), Vue 3 + Element Plus + Axios (Frontend)

---

### Task 1: 创建 Course 实体

**Files:**
- Create: `backend/src/main/java/com/studentaffairs/backend/entity/Course.java`

- [ ] **Step 1: 编写 Course.java**

```java
package com.studentaffairs.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "course")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 20)
    private String type; // 必修/选修

    @Column(nullable = false)
    private Float credit;

    @Column(nullable = false)
    private Integer hours;

    @Column(length = 20)
    private String semester;

    @Column(length = 50)
    private String teacher;

    @Column(name = "class_name", length = 100)
    private String className;
}
```

- [ ] **Step 2: 确认编译通过**

```bash
cd backend && mvn compile -q
```

---

### Task 2: 创建 CourseRepository

**Files:**
- Create: `backend/src/main/java/com/studentaffairs/backend/repository/CourseRepository.java`

- [ ] **Step 1: 编写 CourseRepository.java**

```java
package com.studentaffairs.backend.repository;

import com.studentaffairs.backend.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByType(String type);
    List<Course> findBySemester(String semester);
    List<Course> findByTypeAndSemester(String type, String semester);
}
```

---

### Task 3: 创建 CourseService + CourseController

**Files:**
- Create: `backend/src/main/java/com/studentaffairs/backend/service/CourseService.java`
- Create: `backend/src/main/java/com/studentaffairs/backend/controller/CourseController.java`

- [ ] **Step 1: 编写 CourseService.java**

```java
package com.studentaffairs.backend.service;

import com.studentaffairs.backend.entity.Course;
import com.studentaffairs.backend.repository.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<Course> getAllCourses(String type, String semester) {
        if (type != null && semester != null) {
            return courseRepository.findByTypeAndSemester(type, semester);
        } else if (type != null) {
            return courseRepository.findByType(type);
        } else if (semester != null) {
            return courseRepository.findBySemester(semester);
        }
        return courseRepository.findAll();
    }

    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }

    public Optional<Course> updateCourse(Long id, Course updated) {
        return courseRepository.findById(id).map(c -> {
            c.setCode(updated.getCode());
            c.setName(updated.getName());
            c.setType(updated.getType());
            c.setCredit(updated.getCredit());
            c.setHours(updated.getHours());
            c.setSemester(updated.getSemester());
            c.setTeacher(updated.getTeacher());
            c.setClassName(updated.getClassName());
            return courseRepository.save(c);
        });
    }

    public boolean deleteCourse(Long id) {
        if (courseRepository.existsById(id)) {
            courseRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
```

- [ ] **Step 2: 编写 CourseController.java**

```java
package com.studentaffairs.backend.controller;

import com.studentaffairs.backend.common.Result;
import com.studentaffairs.backend.entity.Course;
import com.studentaffairs.backend.service.CourseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@CrossOrigin(origins = "*")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public Result<List<Course>> getAll(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String semester) {
        return Result.success(courseService.getAllCourses(type, semester));
    }

    @PostMapping
    public Result<Course> create(@RequestBody Course course) {
        return Result.success(courseService.createCourse(course));
    }

    @PutMapping("/{id}")
    public Result<Course> update(@PathVariable Long id, @RequestBody Course course) {
        return courseService.updateCourse(id, course)
                .map(Result::success)
                .orElse(Result.error(404, "课程不存在"));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        if (courseService.deleteCourse(id)) {
            return Result.success(null);
        }
        return Result.error(404, "课程不存在");
    }
}
```

- [ ] **Step 3: 编译验证**

```bash
cd backend && mvn compile -q
```

---

### Task 4: 创建 MidTermAppraisal 实体

**Files:**
- Create: `backend/src/main/java/com/studentaffairs/backend/entity/MidTermAppraisal.java`

- [ ] **Step 1: 编写 MidTermAppraisal.java**

```java
package com.studentaffairs.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "mid_term_appraisal")
public class MidTermAppraisal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false, length = 50)
    private String studentId;

    @Column(name = "student_name", nullable = false, length = 50)
    private String studentName;

    @Column(name = "class_name", nullable = false, length = 50)
    private String className;

    @Column(name = "thought_performance", length = 20)
    private String thoughtPerformance;

    @Column(name = "academic_performance", length = 20)
    private String academicPerformance;

    @Column(name = "overall_performance", length = 20)
    private String overallPerformance;

    @Column(name = "self_assessment", columnDefinition = "TEXT")
    private String selfAssessment;

    @Column(name = "submit_time")
    private LocalDateTime submitTime;
}
```

---

### Task 5: 创建 MidTermAppraisalRepository + MidTermController

**Files:**
- Create: `backend/src/main/java/com/studentaffairs/backend/repository/MidTermAppraisalRepository.java`
- Create: `backend/src/main/java/com/studentaffairs/backend/controller/MidTermController.java`

- [ ] **Step 1: 编写 MidTermAppraisalRepository.java**

```java
package com.studentaffairs.backend.repository;

import com.studentaffairs.backend.entity.MidTermAppraisal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MidTermAppraisalRepository extends JpaRepository<MidTermAppraisal, Long> {
    List<MidTermAppraisal> findByStudentId(String studentId);
    List<MidTermAppraisal> findAllByOrderBySubmitTimeDesc();
}
```

- [ ] **Step 2: 编写 MidTermController.java**

```java
package com.studentaffairs.backend.controller;

import com.studentaffairs.backend.common.Result;
import com.studentaffairs.backend.entity.MidTermAppraisal;
import com.studentaffairs.backend.repository.MidTermAppraisalRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/mid-term")
@CrossOrigin(origins = "*")
public class MidTermController {

    private final MidTermAppraisalRepository repository;

    public MidTermController(MidTermAppraisalRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public Result<MidTermAppraisal> submit(@RequestBody MidTermAppraisal appraisal) {
        appraisal.setSubmitTime(LocalDateTime.now());
        return Result.success(repository.save(appraisal));
    }

    @GetMapping
    public Result<List<MidTermAppraisal>> getByStudent(@RequestParam String studentId) {
        return Result.success(repository.findByStudentId(studentId));
    }

    @GetMapping("/all")
    public Result<List<MidTermAppraisal>> getAll() {
        return Result.success(repository.findAllByOrderBySubmitTimeDesc());
    }

    @GetMapping("/{id}")
    public Result<MidTermAppraisal> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(Result::success)
                .orElse(Result.error(404, "记录不存在"));
    }
}
```

- [ ] **Step 3: 编译验证**

```bash
cd backend && mvn compile -q
```

---

### Task 6: 扩展 AcademicRecordRepository 增加统计查询

**Files:**
- Modify: `backend/src/main/java/com/studentaffairs/backend/repository/AcademicRecordRepository.java`

- [ ] **Step 1: 在已有接口中添加统计方法**

在已有代码末尾的 `}` 之前，添加以下方法：

```java
    @Query("SELECT COUNT(DISTINCT a.studentId) FROM AcademicRecord a")
    Long countDistinctStudents();

    @Query("SELECT AVG(a.score) FROM AcademicRecord a")
    Double calculateOverallAvgScore();
```

---

### Task 7: 扩展 AcademicService 增加统计与成绩 CRUD

**Files:**
- Modify: `backend/src/main/java/com/studentaffairs/backend/service/AcademicService.java`

- [ ] **Step 1: 在已有方法之后添加新方法**

在最后一个方法 `getStudentRadarData` 的 `}` 之后、类的 `}` 之前，添加：

```java
    /** Dashboard 统计 */
    public Map<String, Object> getDashboardStats() {
        long studentCount = recordRepository.countDistinctStudents();
        long warningCount = recordRepository.findByIsWarningTrue().stream()
                .map(AcademicRecord::getStudentId).distinct().count();
        Double avgScore = recordRepository.calculateOverallAvgScore();
        List<AcademicRecord> recentWarnings = recordRepository.findByIsWarningTrue().stream()
                .collect(Collectors.groupingBy(AcademicRecord::getStudentId))
                .entrySet().stream()
                .map(e -> e.getValue().get(0))
                .limit(10)
                .collect(Collectors.toList());

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalStudents", studentCount);
        stats.put("warningCount", warningCount);
        stats.put("avgScore", avgScore != null ? Math.round(avgScore * 100.0) / 100.0 : 0.0);
        stats.put("recentWarnings", recentWarnings);
        return stats;
    }

    /** 新增成绩 */
    public AcademicRecord createRecord(AcademicRecord record) {
        return recordRepository.save(record);
    }

    /** 修改成绩 */
    public Optional<AcademicRecord> updateRecord(Long id, AcademicRecord updated) {
        return recordRepository.findById(id).map(r -> {
            r.setStudentId(updated.getStudentId());
            r.setStudentName(updated.getStudentName());
            r.setClassName(updated.getClassName());
            r.setCourseName(updated.getCourseName());
            r.setCourseType(updated.getCourseType());
            r.setScore(updated.getScore());
            r.setCredit(updated.getCredit());
            r.setSemester(updated.getSemester());
            return recordRepository.save(r);
        });
    }

    /** 删除成绩 */
    public boolean deleteRecord(Long id) {
        if (recordRepository.existsById(id)) {
            recordRepository.deleteById(id);
            return true;
        }
        return false;
    }
```

---

### Task 8: 扩展 AcademicController 增加 Dashboard + 成绩 CRUD 端点

**Files:**
- Modify: `backend/src/main/java/com/studentaffairs/backend/controller/AcademicController.java`

- [ ] **Step 1: 在已有端点之后添加新端点**

在最后一个方法 `getStudentRecords` 的 `}` 之后、类的 `}` 之前，添加：

```java
    /** Dashboard 统计 */
    @GetMapping("/dashboard")
    public Result<Map<String, Object>> getDashboard() {
        return Result.success(academicService.getDashboardStats());
    }

    /** 新增成绩 */
    @PostMapping("/records")
    public Result<AcademicRecord> createRecord(@RequestBody AcademicRecord record) {
        return Result.success(academicService.createRecord(record));
    }

    /** 修改成绩 */
    @PutMapping("/records/{id}")
    public Result<AcademicRecord> updateRecord(@PathVariable Long id, @RequestBody AcademicRecord record) {
        return academicService.updateRecord(id, record)
                .map(Result::success)
                .orElse(Result.error(404, "成绩记录不存在"));
    }

    /** 删除成绩 */
    @DeleteMapping("/records/{id}")
    public Result<Void> deleteRecord(@PathVariable Long id) {
        if (academicService.deleteRecord(id)) {
            return Result.success(null);
        }
        return Result.error(404, "成绩记录不存在");
    }
```

- [ ] **Step 2: 编译验证**

```bash
cd backend && mvn compile -q
```

---

### Task 9: 更新 DataInitializer 增加 Course 种子数据

**Files:**
- Modify: `backend/src/main/java/com/studentaffairs/backend/config/DataInitializer.java`

- [ ] **Step 1: 在 initData 方法参数中添加 CourseRepository**

将方法签名中的参数列表最后添加 `CourseRepository courseRepository`，并在 `initYouthData(...)` 调用之前插入 Course 初始化逻辑：

```java
// 在 initYouthData 调用之前添加
if (courseRepository.count() == 0) {
    List<Course> courses = new ArrayList<>();
    courses.add(createCourse("CS201", "数据结构", "必修", 4f, 64, "2025-2026-1", "张教授", "2023级软工1班、2班"));
    courses.add(createCourse("CS202", "算法设计与分析", "必修", 3f, 48, "2025-2026-1", "李教授", "2023级软工1班、2班"));
    courses.add(createCourse("CS301", "软件工程", "必修", 3f, 48, "2025-2026-1", "王副教授", "2023级软工2班"));
    courses.add(createCourse("CS205", "数据库原理", "必修", 4f, 64, "2025-2026-1", "赵教授", "2023级软工1班、2班"));
    courses.add(createCourse("EN401", "大学英语(四)", "必修", 2f, 32, "2025-2026-1", "刘讲师", "2023级软工1班、2班"));
    courses.add(createCourse("CS250", "Python数据分析", "选修", 2f, 32, "2025-2026-1", "陈副教授", "2023级软工1班"));
    courseRepository.saveAll(courses);
    System.out.println("Initialized default Courses.");
}
```

- [ ] **Step 2: 在 DataInitializer 类中添加 createCourse 辅助方法**

```java
private Course createCourse(String code, String name, String type, Float credit,
                            Integer hours, String semester, String teacher, String className) {
    Course c = new Course();
    c.setCode(code); c.setName(name); c.setType(type);
    c.setCredit(credit); c.setHours(hours); c.setSemester(semester);
    c.setTeacher(teacher); c.setClassName(className);
    return c;
}
```

不要忘记在文件顶部添加 `import com.studentaffairs.backend.entity.Course;` 和 `import com.studentaffairs.backend.repository.CourseRepository;`。

- [ ] **Step 3: 编译验证**

```bash
cd backend && mvn compile -q
```

- [ ] **Step 4: 启动后端验证 API 可用**

```bash
cd backend && mvn spring-boot:run &
sleep 10
# 验证端点
curl -s http://localhost:8080/api/courses | python3 -m json.tool | head -20
curl -s http://localhost:8080/api/academic/dashboard | python3 -m json.tool
```

---

### Task 10: 改造 Academic Dashboard.vue 对接真实 API

**Files:**
- Modify: `frontend/src/views/academic/Dashboard.vue`

- [ ] **Step 1: 替换 mock 数据为 API 调用**

将 `<script setup>` 部分替换为：

```javascript
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Download, DataAnalysis, Warning, Reading, TrendCharts } from '@element-plus/icons-vue'
import axios from 'axios'

const API = 'http://localhost:8080/api/academic'
const router = useRouter()

const stats = ref([
  { label: '学生总数', value: '--', desc: '' },
  { label: '课程总数', value: '--', desc: '' },
  { label: '预警学生', value: '--', desc: '' },
  { label: '平均分', value: '--', desc: '' },
])
const warningStudents = ref([])

onMounted(async () => {
  try {
    const dashRes = await axios.get(`${API}/dashboard`)
    if (dashRes.data.code === 200) {
      const d = dashRes.data.data
      stats.value = [
        { label: '学生总数', value: d.totalStudents, desc: '已录入成绩的学生' },
        { label: '课程总数', value: d.totalCourses || '--', desc: '' },
        { label: '预警学生', value: d.warningCount, desc: '' },
        { label: '平均分', value: d.avgScore, desc: '全体学生均分' },
      ]
      warningStudents.value = (d.recentWarnings || []).map(w => ({
        name: w.studentName,
        studentId: w.studentId,
        className: w.className,
        failedCount: '--',
        level: w.isWarning ? '一般' : '正常',
      }))
    }
  } catch (e) { console.error(e) }
})
```

同时删除原有的硬编码 `const warningStudents = [...]` 数据，将 `stats` 从 `const` 改为 `ref`。

- [ ] **Step 2: 启动前端验证**

```bash
cd frontend && npx vite --port 5173 &
```

访问 `http://localhost:5173/academic`（需先以 academic 角色登录，sessionStorage 中设置 userRole=academic）。

---

### Task 11: 改造 GradeManagement.vue 对接成绩 CRUD API

**Files:**
- Modify: `frontend/src/views/academic/GradeManagement.vue`

- [ ] **Step 1: 替换 script 部分，对接 API**

```javascript
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { UploadFilled, Search } from '@element-plus/icons-vue'
import axios from 'axios'

const API = 'http://localhost:8080/api/academic'

const records = ref([])
const searchQuery = ref(''); const filterSemester = ref(''); const filterClass = ref('')
const importDialogVisible = ref(false)
const editDialogVisible = ref(false)
const editingRecord = ref(null)
const form = ref({ studentId:'', studentName:'', className:'', courseName:'', courseType:'必修', score:0, credit:0, semester:'' })

const loadRecords = async () => {
  try {
    const res = await axios.get(`${API}/student-records`, { params: { studentId: '' } })
    // 获取全部记录：调用无参方式，需要后端支持；这里改用 class-distribution 间接思路
    // 实际使用 warnings 思路不行，先直接 fetch all via recording
    // 最简单: 直接用已有端点 get all records with no filter
    // 目前 AcademicController 没有 getAll，但我们可以通过 class-distribution 不够用
    // 方案：直接用 fetch 从 /api/academic/records 也没 getAll
    // 最简方式：修改 GradeManagement 用 GET /api/academic/all-records
    // 但任务中忘了加。这里直接用 axios.get(`${API}/student-records?studentId=`)
    // 实际我们需要在 controller 加个 getAll。考虑到设计的完整性，这里我们先从已有数据方式处理
  } catch (e) { console.error(e) }
}
```

等一下，我发现一个设计漏洞：AcademicController 缺少一个获取全部成绩记录的端点（GradeManagement 页面需要列出所有成绩）。现有的 `GET /api/academic/student-records?studentId=` 需要 studentId 参数。让我在计划中补上这个。

- [ ] **Step 1a: 先在 AcademicController 中添加获取全部成绩记录端点**

在 `AcademicController.java` 中增加：

```java
    /** 获取全部成绩记录（教务管理） */
    @GetMapping("/records")
    public Result<List<AcademicRecord>> getAllRecords() {
        return Result.success(academicService.getAllRecords());
    }
```

在 `AcademicService.java` 中增加：

```java
    /** 获取全部成绩记录 */
    public List<AcademicRecord> getAllRecords() {
        return recordRepository.findAll();
    }
```

- [ ] **Step 2: 改造 GradeManagement.vue 对接 API**

完整替换 `<script setup>` 部分（包括上面 Step 1a 对应的新端点调用）：

```javascript
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { UploadFilled, Search } from '@element-plus/icons-vue'
import axios from 'axios'

const API = 'http://localhost:8080/api/academic'

const records = ref([])
const searchQuery = ref(''); const filterSemester = ref(''); const filterClass = ref('')
const importDialogVisible = ref(false)
const editDialogVisible = ref(false)
const editingRecord = ref(null)
const form = ref({ studentId:'', studentName:'', className:'', courseName:'', courseType:'必修', score:0, credit:3, semester:'2024-2025-1' })

const loadRecords = async () => {
  try {
    const res = await axios.get(`${API}/records`)
    if (res.data.code === 200) records.value = res.data.data
  } catch (e) { console.error(e) }
}

const semesters = ['2024-2025-1','2024-2025-2','2025-2026-1']
const classes = computed(() => [...new Set(records.value.map(r => r.className))])

const filteredRecords = computed(() => {
  let list = records.value
  if (searchQuery.value) { const q = searchQuery.value.toLowerCase(); list = list.filter(r => r.studentId.includes(q) || r.studentName.includes(q) || r.courseName.includes(q)) }
  if (filterSemester.value) list = list.filter(r => r.semester === filterSemester.value)
  if (filterClass.value) list = list.filter(r => r.className === filterClass.value)
  return list
})

const statList = computed(() => {
  const total = records.value.length
  const failed = records.value.filter(r => r.score < 60).length
  const avg = total > 0 ? (records.value.reduce((a,b)=>a+b.score,0)/total).toFixed(1) : 0
  return [
    { label:'成绩记录', value:total },
    { label:'挂科人次', value:failed },
    { label:'平均分', value:avg },
    { label:'课程数', value:new Set(records.value.map(r=>r.courseName)).size }
  ]
})

const openAddDialog = () => {
  editingRecord.value = null
  form.value = { studentId:'', studentName:'', className:'', courseName:'', courseType:'必修', score:0, credit:3, semester:'2024-2025-1' }
  editDialogVisible.value = true
}

const editGrade = (row) => {
  editingRecord.value = row
  form.value = { ...row }
  editDialogVisible.value = true
}

const saveGrade = async () => {
  if (!form.value.studentId || !form.value.studentName || !form.value.courseName) {
    ElMessage.warning('请填写必要字段'); return
  }
  try {
    if (editingRecord.value) {
      const res = await axios.put(`${API}/records/${editingRecord.value.id}`, form.value)
      if (res.data.code === 200) { ElMessage.success('已更新'); loadRecords() }
    } else {
      const res = await axios.post(`${API}/records`, form.value)
      if (res.data.code === 200) { ElMessage.success('已添加'); loadRecords() }
    }
    editDialogVisible.value = false
  } catch (e) { ElMessage.error('操作失败') }
}

const deleteGrade = async (row) => {
  try {
    await ElMessageBox.confirm(`确定删除 ${row.studentName} 的 ${row.courseName} 成绩？`, '确认删除', { confirmButtonText:'删除', cancelButtonText:'取消', type:'warning' })
    const res = await axios.delete(`${API}/records/${row.id}`)
    if (res.data.code === 200) { ElMessage.success('已删除'); loadRecords() }
  } catch (e) {}
}

onMounted(loadRecords)
```

同时需要在 template 中：为操作列添加删除按钮，并为编辑/新增增加 el-dialog。在 `<el-table-column label="操作">` 中，改为：

```html
<el-table-column label="操作" width="160" fixed="right">
  <template #default="{ row }">
    <button @click="editGrade(row)" class="text-xs font-semibold text-purple-600 hover:text-purple-800 mr-3">修改</button>
    <button @click="deleteGrade(row)" class="text-xs font-semibold text-red-500 hover:text-red-700">删除</button>
  </template>
</el-table-column>
```

在 template 底部（`</div>` 之前、`</template>` 之前）增加编辑对话框：

```html
<el-dialog v-model="editDialogVisible" :title="editingRecord ? '修改成绩' : '新增成绩'" width="520px" destroy-on-close>
  <el-form :model="form" label-position="top">
    <div class="grid grid-cols-2 gap-x-4">
      <el-form-item label="学号" required><el-input v-model="form.studentId" /></el-form-item>
      <el-form-item label="姓名" required><el-input v-model="form.studentName" /></el-form-item>
    </div>
    <el-form-item label="班级"><el-input v-model="form.className" /></el-form-item>
    <div class="grid grid-cols-2 gap-x-4">
      <el-form-item label="课程" required><el-input v-model="form.courseName" /></el-form-item>
      <el-form-item label="类型"><el-select v-model="form.courseType" class="w-full"><el-option label="必修" value="必修" /><el-option label="选修" value="选修" /></el-select></el-form-item>
    </div>
    <div class="grid grid-cols-3 gap-x-4">
      <el-form-item label="成绩"><el-input-number v-model="form.score" :min="0" :max="100" class="w-full" /></el-form-item>
      <el-form-item label="学分"><el-input-number v-model="form.credit" :min="0" :step="0.5" class="w-full" /></el-form-item>
      <el-form-item label="学期"><el-input v-model="form.semester" /></el-form-item>
    </div>
  </el-form>
  <template #footer>
    <button @click="editDialogVisible = false" class="px-4 py-2 text-secondary hover:text-on-surface transition-colors text-sm mr-3">取消</button>
    <button @click="saveGrade" class="px-6 py-2 bg-purple-500 text-white rounded-lg text-sm font-bold hover:bg-purple-600 transition-colors">保存</button>
  </template>
</el-dialog>
```

并在 header-actions 中添加新增按钮：

```html
<button @click="openAddDialog" class="bg-purple-500 text-white hover:bg-purple-600 transition-colors rounded-md px-3.5 py-1.5 text-[0.8125rem] font-semibold flex items-center gap-1 shadow-md mr-2">
  <el-icon :size="14"><Plus /></el-icon>新增成绩
</button>
```

在 import 中增加 `Plus` 图标。

- [ ] **Step 3: 前端编译验证**

```bash
cd frontend && npx vite build --emptyOutDir 2>&1 | tail -5
```

---

### Task 12: 改造 AcademicWarning.vue 对接已有预警 API

**Files:**
- Modify: `frontend/src/views/academic/AcademicWarning.vue`

- [ ] **Step 1: 替换 script 部分对接 API**

```javascript
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { VideoPlay, Search } from '@element-plus/icons-vue'
import axios from 'axios'

const API = 'http://localhost:8080/api/academic'

const searchQuery = ref('')
const warnings = ref([])
const config = ref({ failThreshold: 2, gpaThreshold: 2.0, declineSemesters: 2, autoNotify: true, autoEmail: false })

const loadWarnings = async () => {
  try {
    const res = await axios.get(`${API}/warnings`)
    if (res.data.code === 200) {
      const all = res.data.data
      const byStudent = {}
      all.forEach(r => {
        if (!byStudent[r.studentId]) {
          byStudent[r.studentId] = {
            id: r.id,
            studentId: r.studentId,
            studentName: r.studentName,
            className: r.className,
            failedCount: 0,
            failedCourses: [],
            totalScore: 0,
            totalCredit: 0,
          }
        }
        const s = byStudent[r.studentId]
        s.totalScore += r.score * r.credit
        s.totalCredit += r.credit
        if (r.score < 60) { s.failedCount++; s.failedCourses.push(r.courseName + r.score + '分') }
      })
      warnings.value = Object.values(byStudent).map(s => ({
        id: s.id,
        studentId: s.studentId,
        studentName: s.studentName,
        className: s.className,
        failedCount: s.failedCount,
        gpa: s.totalCredit > 0 ? (s.totalScore / s.totalCredit).toFixed(1) : 0,
        level: s.failedCount >= 2 ? '严重' : '一般',
        reason: s.failedCourses.join('、'),
      }))
    }
  } catch (e) { console.error(e) }
}

const filteredWarnings = computed(() => {
  if (!searchQuery.value) return warnings.value
  const q = searchQuery.value.toLowerCase()
  return warnings.value.filter(w => w.studentId.includes(q) || w.studentName.includes(q))
})

const statList = computed(() => [
  { label:'预警学生', value:warnings.value.length, color:'text-red-500' },
  { label:'严重预警', value:warnings.value.filter(w=>w.level==='严重').length, color:'text-red-500' },
  { label:'一般预警', value:warnings.value.filter(w=>w.level==='一般').length, color:'text-amber-600' },
  { label:'挂科总人次', value:warnings.value.reduce((a,w)=>a+w.failedCount,0), color:'text-green-600' },
])

const runEngine = async () => {
  try {
    const res = await axios.post(`${API}/warnings/run-engine`)
    if (res.data.code === 200) {
      ElMessage.success(res.data.data)
      loadWarnings()
    }
  } catch (e) { ElMessage.error('预警引擎运行失败') }
}

const saveConfig = () => { ElMessage.success('预警配置已保存') }
const viewDetail = (row) => { ElMessage.info(`${row.studentName}：${row.reason}`) }

onMounted(loadWarnings)
```

---

### Task 13: 改造 CourseManagement.vue 对接课程 API

**Files:**
- Modify: `frontend/src/views/academic/CourseManagement.vue`

- [ ] **Step 1: 替换 script 部分对接 API**

```javascript
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'
import axios from 'axios'

const API = 'http://localhost:8080/api/courses'

const courses = ref([])
const searchQuery = ref(''); const filterType = ref(''); const filterSemester = ref('')
const dialogVisible = ref(false); const editingCourse = ref(null)
const form = ref({ code:'', name:'', type:'必修', credit:3, hours:48, semester:'2025-2026-1', teacher:'', className:'' })

const loadCourses = async () => {
  try {
    const params = {}
    if (filterType.value) params.type = filterType.value
    if (filterSemester.value) params.semester = filterSemester.value
    const res = await axios.get(API, { params })
    if (res.data.code === 200) courses.value = res.data.data
  } catch (e) { console.error(e) }
}

const semesters = ['2024-2025-2','2025-2026-1','2025-2026-2']

const filteredCourses = computed(() => {
  let list = courses.value
  if (searchQuery.value) { const q = searchQuery.value.toLowerCase(); list = list.filter(c => c.name.includes(q) || c.code.toLowerCase().includes(q)) }
  return list
})

const statList = computed(() => {
  const required = courses.value.filter(c => c.type === '必修').length
  const elective = courses.value.filter(c => c.type === '选修').length
  return [
    { label:'课程总数', value:courses.value.length },
    { label:'必修课', value:required },
    { label:'选修课', value:elective },
    { label:'总学分', value:courses.value.reduce((a,c)=>a+c.credit,0) }
  ]
})

const openAddDialog = () => {
  editingCourse.value = null
  form.value = { code:'', name:'', type:'必修', credit:3, hours:48, semester:'2025-2026-1', teacher:'', className:'' }
  dialogVisible.value = true
}

const editCourse = (row) => { editingCourse.value = row; form.value = { ...row }; dialogVisible.value = true }

const saveCourse = async () => {
  if (!form.value.code || !form.value.name) { ElMessage.warning('课程代码和名称为必填'); return }
  try {
    if (editingCourse.value) {
      const res = await axios.put(`${API}/${editingCourse.value.id}`, form.value)
      if (res.data.code === 200) { ElMessage.success('课程已更新'); loadCourses() }
      else { ElMessage.error(res.data.msg) }
    } else {
      const res = await axios.post(API, form.value)
      if (res.data.code === 200) { ElMessage.success('课程已添加'); loadCourses() }
    }
    dialogVisible.value = false
  } catch (e) { ElMessage.error('操作失败') }
}

const deleteCourse = async (row) => {
  try {
    await ElMessageBox.confirm(`确定删除课程「${row.name}」？`, '确认删除', { confirmButtonText:'删除', cancelButtonText:'取消', type:'warning' })
    const res = await axios.delete(`${API}/${row.id}`)
    if (res.data.code === 200) { ElMessage.success('已删除'); loadCourses() }
  } catch (e) {}
}

// 监听筛选条件变化重新加载
import { watch } from 'vue'
watch([filterType, filterSemester], () => loadCourses())

onMounted(loadCourses)
```

注意：Template 中筛选区的 `el-select` 已有，不再需要本地过滤，改为调用 API。所以 `filteredCourses` 直接返回 `courses.value`。删除原有的本地过滤逻辑，添加 `watch` 监听筛选变化。

---

### Task 14: 改造 MidTermManagement.vue（教务端查看）

**Files:**
- Modify: `frontend/src/views/academic/MidTermManagement.vue`

- [ ] **Step 1: 替换 script 部分对接 API（只做查看，去掉审核按钮）**

```javascript
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { DocumentChecked, Search } from '@element-plus/icons-vue'
import axios from 'axios'

const API = 'http://localhost:8080/api/mid-term'

const searchQuery = ref(''); const detailVisible = ref(false); const detail = ref(null)
const list = ref([])

const loadList = async () => {
  try {
    const res = await axios.get(`${API}/all`)
    if (res.data.code === 200) list.value = res.data.data
  } catch (e) { console.error(e) }
}

const filteredList = computed(() => {
  let l = list.value
  if (searchQuery.value) { const q = searchQuery.value.toLowerCase(); l = l.filter(x => x.studentName.includes(q) || x.studentId.includes(q)) }
  return l
})

const stats = computed(() => [
  { label:'总提交数', value:list.value.length },
  { label:'今日提交', value:list.value.filter(x => {
    const today = new Date().toDateString()
    return x.submitTime && new Date(x.submitTime).toDateString() === today
  }).length },
  { label:'涉及班级', value:new Set(list.value.map(x=>x.className)).size },
  { label:'涉及学生', value:new Set(list.value.map(x=>x.studentId)).size },
])

const scoreColor = (s) => ({ '优秀':'text-green-600','良好':'text-blue-600','合格':'text-amber-600','待改进':'text-red-500' }[s]||'')
const viewDetail = (row) => { detail.value = row; detailVisible.value = true }

onMounted(loadList)
```

删除 `handleSelectionChange`、`approve`、`reject`、`batchReview` 等审核相关逻辑。Template 中移除：
- `<el-table-column type="selection">`
- 操作列中的"通过"/"退回"按钮，只保留"查看"
- header-actions 中的"批量审核"按钮
- 状态列和状态筛选（MidTermAppraisal 无状态字段）

Template 中操作列改为只需"查看"：

```html
<el-table-column label="操作" width="80" fixed="right">
  <template #default="{ row }">
    <button @click="viewDetail(row)" class="text-xs font-semibold text-purple-600 hover:text-purple-800">查看</button>
  </template>
</el-table-column>
```

---

### Task 15: 改造学生端 MidTermAppraisal.vue 对接提交 API

**Files:**
- Modify: `frontend/src/views/student/MidTermAppraisal.vue`

- [ ] **Step 1: 修改 submitForm 方法，调用后端 API**

将 `submitForm` 中的 `localStorage` 逻辑替换为 axios 调用。在 script 顶部添加：

```javascript
import axios from 'axios'
const API = 'http://localhost:8080/api/mid-term'
```

将 `submitForm` 函数中的提交逻辑替换为：

```javascript
const submitForm = () => {
  if (completedQuestions.value < questions.value.length) {
    ElMessage.error(`请确保所有主观题（共 ${questions.value.length} 道）均不少于 100 字后再提交`)
    return
  }
  if (!form.value.thoughtEval || !form.value.academicEval || !form.value.comprehensiveEval) {
    ElMessage.error('请完成所有自评等级选择')
    return
  }

  ElMessageBox.confirm('正式提交后将无法修改，是否确认提交？', '提交确认', {
    confirmButtonText: '确认提交',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    submitting.value = true
    try {
      const payload = {
        studentId: '202301042',  // TODO: 后续从登录状态获取
        studentName: '张小明',
        className: '软工2班',
        thoughtPerformance: form.value.thoughtEval,
        academicPerformance: form.value.academicEval,
        overallPerformance: form.value.comprehensiveEval,
        selfAssessment: questions.value.map((q, i) => `${q.title}: ${answers.value[i]}`).join('\n\n'),
      }
      const res = await axios.post(API, payload)
      if (res.data.code === 200) {
        ElMessage.success('中期鉴定已成功提交！')
        isSubmitted.value = true
        localStorage.setItem('midterm_submitted', 'true')
      } else {
        ElMessage.error(res.data.msg || '提交失败')
      }
    } catch (e) {
      ElMessage.error('提交失败，请检查网络连接')
    }
    submitting.value = false
  })
}
```

在 `onMounted` 中增加从 API 加载已有提交的逻辑：

```javascript
// 在 onMounted 末尾添加
const loadExisting = async () => {
  try {
    const res = await axios.get(`${API}?studentId=202301042`)
    if (res.data.code === 200 && res.data.data.length > 0) {
      isSubmitted.value = true
      localStorage.setItem('midterm_submitted', 'true')
    }
  } catch (e) {}
}
loadExisting()
```

---

### Task 16: 新增辅导员端 MidTermManagement.vue（左右分栏布局）

**Files:**
- Create: `frontend/src/views/teacher/MidTermManagement.vue`
- Modify: `frontend/src/router/index.js`

- [ ] **Step 1: 编写 teacher/MidTermManagement.vue**

参照 Communication.vue 的布局，写入完整的 Vue SFC。核心结构：

```vue
<template>
  <div class="h-[calc(100vh-72px)] lg:h-[calc(100vh-104px)] w-full flex gap-6 -mb-4">
    <!-- 左侧学生列表 -->
    <div class="w-80 flex-shrink-0 flex flex-col bg-white/50 backdrop-blur-xl rounded-2xl border border-white/60 shadow-sm overflow-hidden">
      <div class="px-3 pt-2 pb-2">
        <div class="relative">
          <span class="absolute left-3 top-1/2 -translate-y-1/2 text-outline z-10"><el-icon :size="14"><Search /></el-icon></span>
          <input v-model="searchQuery" placeholder="搜索学生姓名或学号..."
                 class="w-full pl-9 pr-3 py-2 rounded-xl border border-outline-variant/30 bg-white text-sm outline-none focus:border-primary/50 focus:ring-2 focus:ring-primary/20 transition-all" />
        </div>
      </div>
      <div class="h-px bg-outline-variant/15"></div>
      <div class="flex-1 overflow-y-auto custom-scrollbar p-2 space-y-1">
        <div v-for="student in filteredStudents" :key="student.studentId"
             @click="selectStudent(student)"
             class="p-3 rounded-xl cursor-pointer transition-all border flex items-center gap-3"
             :class="activeStudentId === student.studentId ? 'bg-white shadow-sm border-primary/30 ring-1 ring-primary/20' : 'border-transparent hover:bg-white hover:shadow-md hover:border-outline-variant/30'">
          <div class="w-10 h-10 rounded-full bg-purple-100 flex items-center justify-center text-purple-600 font-bold text-sm flex-shrink-0">
            {{ student.name[0] }}
          </div>
          <div class="flex-1 min-w-0">
            <div class="font-bold text-sm text-on-surface truncate">{{ student.name }}</div>
            <div class="text-xs text-outline truncate">{{ student.studentId }} · {{ student.className }}</div>
          </div>
          <div class="flex-shrink-0">
            <span v-if="student.hasSubmitted" class="text-xs font-bold px-2 py-0.5 rounded-full bg-green-100 text-green-700">已提交</span>
            <span v-else class="text-xs font-bold px-2 py-0.5 rounded-full bg-gray-100 text-gray-500">未提交</span>
          </div>
        </div>
        <div v-if="filteredStudents.length === 0" class="py-10 text-center text-secondary text-sm">暂无学生</div>
      </div>
    </div>

    <!-- 右侧详情面板 -->
    <div class="flex-1 bg-white/50 backdrop-blur-xl rounded-2xl border border-white/60 shadow-sm overflow-hidden flex flex-col">
      <div v-if="activeAppraisal" class="flex flex-col h-full">
        <div class="px-6 py-4 border-b border-outline-variant/30 bg-white/40">
          <h3 class="font-bold text-base text-on-surface">{{ activeAppraisal.studentName }}</h3>
          <p class="text-xs text-secondary mt-0.5">{{ activeAppraisal.studentId }} · {{ activeAppraisal.className }} · {{ formatTime(activeAppraisal.submitTime) }}</p>
        </div>
        <div class="flex-1 overflow-y-auto p-6 space-y-4 custom-scrollbar">
          <div class="bg-surface-container-low rounded-xl p-4">
            <p class="text-xs font-bold text-secondary uppercase mb-3">自评等级</p>
            <div class="grid grid-cols-3 gap-4 text-sm">
              <div><span class="text-secondary">思想表现：</span><span class="font-bold" :class="scoreColor(activeAppraisal.thoughtPerformance)">{{ activeAppraisal.thoughtPerformance }}</span></div>
              <div><span class="text-secondary">学业成绩：</span><span class="font-bold" :class="scoreColor(activeAppraisal.academicPerformance)">{{ activeAppraisal.academicPerformance }}</span></div>
              <div><span class="text-secondary">综合表现：</span><span class="font-bold" :class="scoreColor(activeAppraisal.overallPerformance)">{{ activeAppraisal.overallPerformance }}</span></div>
            </div>
          </div>
          <div class="bg-surface-container-low rounded-xl p-4">
            <p class="text-xs font-bold text-secondary uppercase mb-3">自我鉴定</p>
            <p class="text-sm leading-relaxed whitespace-pre-wrap">{{ activeAppraisal.selfAssessment }}</p>
          </div>
        </div>
      </div>
      <div v-else class="flex flex-col items-center justify-center h-full text-secondary">
        <el-icon :size="64" class="mb-4 opacity-20"><DocumentChecked /></el-icon>
        <p class="text-lg font-medium text-on-surface">中期鉴定收集</p>
        <p class="text-sm mt-2 opacity-80">选择左侧学生查看鉴定详情</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Search, DocumentChecked } from '@element-plus/icons-vue'
import axios from 'axios'

const API = 'http://localhost:8080/api/mid-term'
const teacherId = sessionStorage.getItem('userId') || 'T001'

const searchQuery = ref('')
const activeStudentId = ref(null)
const students = ref([])
const allAppraisals = ref([])

const loadStudents = async () => {
  try {
    const res = await axios.get('http://localhost:8080/api/communication/students', { params: { teacherId } })
    if (res.data.code === 200) students.value = res.data.data
  } catch (e) { console.error(e) }
}

const loadAppraisals = async () => {
  try {
    const res = await axios.get(`${API}/all`)
    if (res.data.code === 200) allAppraisals.value = res.data.data
  } catch (e) { console.error(e) }
}

const filteredStudents = computed(() => {
  let list = students.value.map(s => {
    const appraisal = allAppraisals.value.find(a => a.studentId === s.studentId)
    return { ...s, hasSubmitted: !!appraisal, appraisalId: appraisal?.id }
  })
  if (!searchQuery.value) return list
  const q = searchQuery.value.toLowerCase()
  return list.filter(s => s.name.toLowerCase().includes(q) || s.studentId.toLowerCase().includes(q))
})

const activeAppraisal = computed(() => {
  return allAppraisals.value.find(a => a.studentId === activeStudentId.value) || null
})

const selectStudent = (student) => {
  activeStudentId.value = student.studentId
}

const formatTime = (s) => {
  if (!s) return ''
  return new Date(s).toLocaleString('zh-CN', { year:'numeric', month:'2-digit', day:'2-digit', hour:'2-digit', minute:'2-digit' })
}

const scoreColor = (s) => ({ '优秀':'text-green-600','良好':'text-blue-600','合格':'text-amber-600','待改进':'text-red-500' }[s]||'')

onMounted(async () => {
  await Promise.all([loadStudents(), loadAppraisals()])
})
</script>
```

- [ ] **Step 2: 注册路由**

在 `router/index.js` 的 `/teacher` children 数组中添加：

```javascript
{ path: 'mid-term', name: 'TeacherMidTerm', meta: { title: '中期鉴定收集' }, component: () => import('../views/teacher/MidTermManagement.vue') }
```

- [ ] **Step 3: 在 Teacher Layout 侧边栏添加导航**

在 `teacher/Layout.vue` 的侧边栏 `<ul>` 中适当位置添加：

```html
<li>
  <router-link to="/teacher/mid-term" active-class="!text-primary font-bold"
    class="w-full flex items-center gap-3 px-4 py-3 rounded-xl transition-all text-secondary hover:bg-white/60 group">
    <el-icon :size="20"><DocumentChecked /></el-icon>中期鉴定收集
  </router-link>
</li>
```

需在 script import 中增加 `DocumentChecked` 图标。

---

### Task 17: 集成测试 — 启动前后端验证完整流程

**Files:** None (验证)

- [ ] **Step 1: 确保后端运行**

```bash
cd backend && mvn spring-boot:run &
sleep 15
```

- [ ] **Step 2: 验证所有新增端点**

```bash
# Dashboard 统计
curl -s http://localhost:8080/api/academic/dashboard | python3 -m json.tool

# 成绩 CRUD
curl -s http://localhost:8080/api/academic/records | python3 -m json.tool | head -10
curl -s -X POST http://localhost:8080/api/academic/records -H 'Content-Type: application/json' -d '{"studentId":"202301042","studentName":"张小明","className":"软工2班","courseName":"测试课程","courseType":"必修","score":85,"credit":2,"semester":"2024-2025-1"}' | python3 -m json.tool

# 课程 CRUD
curl -s http://localhost:8080/api/courses | python3 -m json.tool | head -10
curl -s -X POST http://localhost:8080/api/courses -H 'Content-Type: application/json' -d '{"code":"TEST01","name":"测试课程","type":"选修","credit":2,"hours":32,"semester":"2025-2026-1","teacher":"测试教师","className":"测试班"}' | python3 -m json.tool

# 中期鉴定
curl -s http://localhost:8080/api/mid-term/all | python3 -m json.tool
curl -s -X POST http://localhost:8080/api/mid-term -H 'Content-Type: application/json' -d '{"studentId":"202301042","studentName":"张小明","className":"软工2班","thoughtPerformance":"优秀","academicPerformance":"优秀","overallPerformance":"优秀","selfAssessment":"测试鉴定内容"}' | python3 -m json.tool
```

- [ ] **Step 3: 启动前端验证页面渲染**

```bash
cd frontend && npx vite --port 5173 &
```

在浏览器中设置 `sessionStorage.setItem('userRole', 'academic')` 后访问各页面，确认数据从 API 加载。同样测试 student 和 teacher 角色的中期鉴定功能。

---

### Task 18: 提交所有变更

- [ ] **Step 1: 检查变更并提交**

```bash
git status
git add backend/src/main/java/com/studentaffairs/backend/entity/Course.java
git add backend/src/main/java/com/studentaffairs/backend/entity/MidTermAppraisal.java
git add backend/src/main/java/com/studentaffairs/backend/repository/CourseRepository.java
git add backend/src/main/java/com/studentaffairs/backend/repository/MidTermAppraisalRepository.java
git add backend/src/main/java/com/studentaffairs/backend/service/CourseService.java
git add backend/src/main/java/com/studentaffairs/backend/controller/CourseController.java
git add backend/src/main/java/com/studentaffairs/backend/controller/MidTermController.java
git add backend/src/main/java/com/studentaffairs/backend/controller/AcademicController.java
git add backend/src/main/java/com/studentaffairs/backend/service/AcademicService.java
git add backend/src/main/java/com/studentaffairs/backend/repository/AcademicRecordRepository.java
git add backend/src/main/java/com/studentaffairs/backend/config/DataInitializer.java
git add frontend/src/views/academic/Dashboard.vue
git add frontend/src/views/academic/GradeManagement.vue
git add frontend/src/views/academic/AcademicWarning.vue
git add frontend/src/views/academic/CourseManagement.vue
git add frontend/src/views/academic/MidTermManagement.vue
git add frontend/src/views/student/MidTermAppraisal.vue
git add frontend/src/views/teacher/MidTermManagement.vue
git add frontend/src/router/index.js
git add frontend/src/views/teacher/Layout.vue
git commit -m "feat: connect academic affairs module with real APIs, add course/mid-term management"
```
