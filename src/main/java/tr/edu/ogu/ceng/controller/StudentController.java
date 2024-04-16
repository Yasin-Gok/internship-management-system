package tr.edu.ogu.ceng.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import tr.edu.ogu.ceng.dto.StudentDto;
import tr.edu.ogu.ceng.dto.requests.StudentRequestDto;
import tr.edu.ogu.ceng.dto.responses.StudentResponseDto;
import tr.edu.ogu.ceng.model.Student;
import tr.edu.ogu.ceng.model.User;
import tr.edu.ogu.ceng.service.AuthenticationService;
import tr.edu.ogu.ceng.service.StudentService;
import tr.edu.ogu.ceng.util.PageableUtil;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    @Autowired
    StudentService studentService;
    
    @Autowired 
    AuthenticationService authenticationService;

    @GetMapping("/{id}")
    public ResponseEntity<StudentDto> getStudent() {
    	 Long userId = authenticationService.getCurrentUserId();

         if (userId == null) {
             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
         }

         StudentDto studentResponseDto = studentService.getStudentByUserId(userId);
         return ResponseEntity.ok(studentResponseDto);
    }

    @GetMapping("/getAll")
    public Page<StudentResponseDto> getStudents(@RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer limit, @RequestParam(defaultValue = "name") String sortBy) {
        Pageable pageable = PageableUtil.createPageRequest(pageNo, limit, sortBy);
        Page<StudentResponseDto> students = studentService.getAllStudents(pageable);
        return students;
    }

    /**
     * Search student by name, surname or student number.
     * 
     * @param keyWord
     * @param pageNo
     * @param limit
     * @param sortBy
     * @return Page<StudentResponseDto>
     */
    @GetMapping("/search/{keyWord}")
    public Page<StudentResponseDto> searchStudent(@PathVariable(name = "keyWord") String keyWord,
            @RequestParam(defaultValue = "0") Integer pageNo, @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(defaultValue = "name") String sortBy) {
        Pageable pageable = PageableUtil.createPageRequest(pageNo, limit, sortBy);
        Page<StudentResponseDto> students = studentService.searchStudent(pageable, keyWord);
        return students;
    }

    @PostMapping
    public StudentResponseDto addStudent(@RequestBody StudentRequestDto studentRequestDto) {
        ModelMapper modelMapper = new ModelMapper();
        User user = modelMapper.map(studentRequestDto.getUser(), User.class);
        Student student = modelMapper.map(studentRequestDto, Student.class);
        student.setUser(user);
        return modelMapper.map(studentService.addStudent(student), StudentResponseDto.class);
    }

    @PutMapping
    public StudentResponseDto updateStudent(@RequestBody StudentRequestDto studentDto) {
        return studentService.updateStudent(studentDto);
    }

    @DeleteMapping("/{id}")
    public boolean deleteStudent(@PathVariable(name = "id") Long id) {
        return studentService.deleteStudent(id);
    }

    @PostMapping("/registerasstudent")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ResponseEntity<StudentResponseDto> registerAsStudent(@RequestBody StudentDto request) {
        StudentResponseDto response = studentService.registerAsStudent(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/byUserId")
    public ResponseEntity<StudentDto> getStudentByUserId() {
        Long userId = authenticationService.getCurrentUserId();

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        StudentDto studentDto = studentService.getStudentByUserId(userId);
        if (studentDto != null) {
            return ResponseEntity.ok(studentDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/supervisor/{id}")
    public Page<StudentResponseDto> getAllStudentsByFacultySupervisorId(@PathVariable("id") Long faculty_supervisor_id,
            @RequestParam(defaultValue = "0") Integer pageNo, @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(defaultValue = "id") String sortBy) {
        Pageable pageable = PageableUtil.createPageRequest(pageNo, limit, sortBy);
        Page<StudentResponseDto> students = studentService.getAllStudentsByFacultySupervisorId(faculty_supervisor_id,
                pageable);
        return students;
    }
    @GetMapping("/count")
	public ResponseEntity<Long> getCompanyCount() {
		Long count = studentService.countStudents();
		return ResponseEntity.ok(count);
	}
}
