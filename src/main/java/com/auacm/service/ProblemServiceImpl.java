package com.auacm.service;

import com.auacm.api.model.request.CreateProblemRequest;
import com.auacm.api.model.response.CreateProblemResponse;
import com.auacm.database.dao.ProblemDao;
import com.auacm.database.dao.ProblemDataDao;
import com.auacm.database.dao.SampleCaseDao;
import com.auacm.database.model.Competition;
import com.auacm.database.model.Problem;
import com.auacm.exception.ProblemNotFoundException;
import com.auacm.util.JsonUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProblemServiceImpl implements ProblemService {

    @Autowired
    private ProblemDao problemDao;

    @Autowired
    private SolvedProblemService solvedProblemService;

    @Autowired
    private CompetitionService competitionService;

    @Autowired
    private FileSystemService fileSystemService;

    @Autowired
    private ProblemDataDao problemDataDao;

    @Autowired
    private SampleCaseDao sampleCaseDao;

    @Autowired
    private JsonUtil jsonUtil;

    @Autowired
    private Gson gson;

    public ProblemServiceImpl() {}

    @Override
    public CreateProblemResponse createProblem(CreateProblemRequest problemRequest) {
        if (!fileSystemService.doesFileExist("data/problems/")) {
            fileSystemService.createFolder("data/problems/");
        }
        if (problemRequest.getImportZip() == null || problemRequest.getImportZip().isEmpty()) {
            Problem newProblem = new Problem(problemRequest);
            setProblemCompetition(problemRequest, newProblem);
            newProblem.setShortName(getShortName(problemRequest.getName()));
            newProblem = problemDao.save(newProblem);


            if (problemRequest.getInputZip() != null) {
                fileSystemService.saveProblemInputZip(newProblem.getPid() + "", problemRequest.getInputZip());
            }

            if (problemRequest.getOutputZip() != null) {
                fileSystemService.saveProblemOutputZip(newProblem.getPid() + "", problemRequest.getOutputZip());
            }

            if (problemRequest.getInputFiles() != null) {
                int index = 1;
                for (MultipartFile file : problemRequest.getInputFiles()) {
                    fileSystemService.saveProblemInputFile(newProblem.getPid() + "", file, String.format("in%d.txt", index));
                    index++;
                }
            }

            if (problemRequest.getOutputFiles() != null) {
                int index = 1;
                for (MultipartFile file : problemRequest.getOutputFiles()) {
                    fileSystemService.saveProblemOutputFile(newProblem.getPid() + "", file, String.format("out%d.txt", index));
                    index++;
                }
            }

            CreateProblemResponse problemResponse = new CreateProblemResponse(newProblem);

            fileSystemService.saveSolutionFile(newProblem.getPid() + "", problemRequest.getSolutionFile());
            fileSystemService.createProblemZip(newProblem.getPid() + "", gson.toJsonTree(problemResponse).getAsJsonObject());
            return problemResponse;
        } else {
            return saveProblemZip(problemRequest);
        }
    }

    private String getShortName(String name) {
        String shortName = name.toLowerCase().replaceAll(" ", "");
        int index = 0;
        Optional<Problem> problem = problemDao.findByShortNameIgnoreCase(shortName);
        while (problem.isPresent()) {
            index++;
            problem = problemDao.findByShortNameIgnoreCase(shortName + index);
        }
        return shortName + (index > 0 ? index : "");
    }

    @Override
    @Transactional
    public Problem addProblem(Problem problem) {
        return problemDao.save(problem);
    }

    @Override
    @Transactional
    public Problem updateProblem(Problem problem) {
        return problemDao.save(problem);
    }

    @Override
    @Transactional
    public CreateProblemResponse updateProblem(String identifier, CreateProblemRequest problemRequest) {
        Problem updateProblem = getProblem(identifier);
        if (problemRequest.getImportZip() == null || problemRequest.getImportZip().isEmpty()) {
            updateProblem.update(problemRequest);
            setProblemCompetition(problemRequest, updateProblem);
            if (problemRequest.getName() != null) {
                updateProblem.setShortName(getShortName(problemRequest.getName()));
            }

            updateProblem = problemDao.save(updateProblem);

            // Update problem data
//            ProblemData problemData = problemDataDao.getOne(problem1.getPid());
//            if (problem.getDescription() != null) {
//                problemData.setDescription(problem.getDescription());
//            }
//            if (problem.getInputDesc() != null) {
//                problemData.setInputDescription(problem.getInputDesc());
//            }
//            if (problem.getOutputDesc() != null) {
//                problemData.setOutputDescription(problem.getOutputDesc());
//            }
//            if (problem.getTimeLimit() != null) {
//                problemData.setTimeLimit(problem.getTimeLimit());
//            } else {
                // TODO Run script and get time limit
                // Setting the default time limit to 2 seconds
//                problemData.setTimeLimit(2L);
//            }
//            problemDataDao.save(problemData);

//            ArrayList<SampleCase> sampleCases = new ArrayList<>();
//            if (problem.getSampleCaseList() != null) {
//                sampleCaseDao.deleteAllBySampleCasePK_Pid(problem1.getPid());
//                for (com.auacm.api.model.SampleCase sampleCase : problem.getSampleCaseList()) {
//                    SampleCase newSampleCase = new SampleCase();
//                    newSampleCase.getSampleCasePK().setCaseNum(sampleCase.getCaseNum());
//                    newSampleCase.setInput(sampleCase.getInput());
//                    newSampleCase.setOutput(sampleCase.getOutput());
//                    sampleCases.add(newSampleCase);
//                    newSampleCase.getSampleCasePK().setPid(problem1.getPid());
//                }
//                sampleCaseDao.saveAll(sampleCases);
//                problem1.setSampleCases(sampleCases);
//            } else {
//                problem1.setSampleCases(sampleCaseDao.findAllBySampleCasePK_Pid(problem1.getPid()));
//            }

            if (problemRequest.getInputZip() != null) {
                fileSystemService.saveProblemInputZip(updateProblem.getPid() + "", problemRequest.getInputZip());
            }

            if (problemRequest.getOutputZip() != null) {
                fileSystemService.saveProblemOutputZip(updateProblem.getPid() + "", problemRequest.getOutputZip());
            }

            if (problemRequest.getInputFiles() != null) {
                int index = 1;
                for (MultipartFile file : problemRequest.getInputFiles()) {
                    fileSystemService.saveProblemInputFile(updateProblem.getPid() + "", file, String.format("in%d.txt", index));
                    index++;
                }
            }

            if (problemRequest.getOutputFiles() != null) {
                int index = 1;
                for (MultipartFile file : problemRequest.getOutputFiles()) {
                    fileSystemService.saveProblemOutputFile(updateProblem.getPid() + "", file, String.format("out%d.txt", index));
                    index++;
                }
            }
            if (problemRequest.getSolutionFile() != null) {
                fileSystemService.saveSolutionFile(updateProblem.getPid() + "", problemRequest.getSolutionFile());
            }
        } else {
            // TODO Import the zip
        }
        CreateProblemResponse problemResponse = new CreateProblemResponse(updateProblem);
        fileSystemService.createProblemZip(updateProblem.getPid() + "", gson.toJsonTree(problemResponse).getAsJsonObject());
        return problemResponse;
    }

    @Override
    @Transactional
    public void deleteProblem(Problem problem) {
        problemDao.delete(problem);
    }

    @Override
    @Transactional
    public void deleteProblem(String identifier) {
        try {
            Long id = Long.parseLong(identifier);
            problemDao.deleteById(id);
        } catch (NumberFormatException e) {
            problemDao.deleteByShortName(identifier);
        }
    }

    @Override
    public List<Problem> getAllProblems() {
        if (SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return problemDao.findAll();
        } else {
            List<Problem> allProblems = problemDao.findAll();
            List<Problem> notInUpcoming = new ArrayList<>();
            for (Problem problem : allProblems) {
                if (!competitionService.isInUpcomingCompetition(problem)) {
                    notInUpcoming.add(problem);
                }
            }
            return notInUpcoming;
        }
    }

    @Override
    public Problem getProblem(String identifier) {
        try {
            long pid = Integer.parseInt(identifier);
            return getProblemForPid(pid);
        } catch (NumberFormatException e) {
            return getProblemForShortName(identifier);
        }
    }

    @Override
    @Transactional
    public Problem getProblemForPid(long pid) {
        Optional<Problem> problem = problemDao.findById(pid);
        if (!problem.isPresent()) {
            throw new ProblemNotFoundException("Failed to find a problem for pid " + pid + ".");
        }
        if (competitionService.isInUpcomingCompetition(problem.get())) {
            if (SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                    .contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
                return problem.get();
            } else {
                throw new ProblemNotFoundException("Failed to find a problem for pid " + pid + ".");
            }
        } else {
            return problem.get();
        }
    }

    @Override
    @Transactional
    public Problem getProblemForShortName(String shortName) {
        Optional<Problem> problem = problemDao.findByShortNameIgnoreCase(shortName);
        if (!problem.isPresent()) {
            throw new ProblemNotFoundException("Failed to find a problem for shortname " + shortName + ".");
        }
        if (competitionService.isInUpcomingCompetition(problem.get())) {
            if (SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                    .contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
                return problem.get();
            } else {
                throw new ProblemNotFoundException("Failed to find a problem for shortname " + shortName + ".");
            }
        } else {
            return problem.get();
        }
    }

    private Problem setProblemCompetition(CreateProblemRequest problemRequest, Problem problem) {
        if (problemRequest.getAppearedIn() != null) {
            Competition competition = competitionService.getCompetitionByName(problemRequest.getAppearedIn());
            if (competition != null) {
                problem.setAppeared(problemRequest.getAppearedIn());
                problem.setCompetitionId(competition.getCid());
            } else {
                problem.setAppeared("");
            }
        } else {
            problem.setAppeared("");
        }
        return problem;
    }

    private Problem setProblemCompetition(JsonObject problemRequest, Problem problem) {
        if (problemRequest.has("appearedIn")) {
            Competition competition = competitionService.getCompetitionByName(problemRequest.get("appearedIn").getAsString());
            if (competition != null) {
                problem.setAppeared(problemRequest.get("appearedIn").getAsString());
                problem.setCompetitionId(competition.getCid());
            } else {
                problem.setAppeared("");
            }
        } else {
            problem.setAppeared("");
        }
        return problem;
    }

    private CreateProblemResponse saveProblemZip(CreateProblemRequest problem) {
        long currentTime = System.currentTimeMillis();
        if (fileSystemService.saveTempFile(problem.getImportZip(),
                currentTime + "", problem.getImportZip().getOriginalFilename())) {
            fileSystemService.unzipFile(String.format("%s%s/%s", fileSystemService.getTmpFolder(),
                    currentTime + "", problem.getImportZip().getOriginalFilename()));
            String data = fileSystemService.getFileContents(String.format("%s%s/data.json",
                    fileSystemService.getTmpFolder(), currentTime + ""));
            if (data != null) {
                JsonObject object = new JsonParser().parse(data).getAsJsonObject();
                if (object.get("exportVersion").getAsInt() == 1) {
                    Problem newProblem = new Problem(object.get("data").getAsJsonObject());
                    newProblem.setShortName(getShortName(newProblem.getName()));
                    setProblemCompetition(object.getAsJsonObject("data"), newProblem);

                    newProblem = problemDao.save(newProblem);

                    CreateProblemResponse problemResponse = new CreateProblemResponse(newProblem);

                    fileSystemService.moveFile(String.format("%s%s/in/", fileSystemService.getTmpFolder(),
                            currentTime + ""), String.format("data/problems/%d/in/", problemResponse.getPid()));
                    fileSystemService.moveFile(String.format("%s%s/out/", fileSystemService.getTmpFolder(),
                            currentTime + ""), String.format("data/problems/%d/out/", problemResponse.getPid()));
                    fileSystemService.moveFile(String.format("%s%s/test/", fileSystemService.getTmpFolder(),
                            currentTime + ""), String.format("data/problems/%d/test/", problemResponse.getPid()));
                    fileSystemService.createProblemZip(problemResponse.getPid() + "",
                            gson.toJsonTree(problemResponse).getAsJsonObject());
                    fileSystemService.deleteFile(String.format("%s%s", fileSystemService.getTmpFolder(), currentTime + ""));
                    return problemResponse;
                } else {
                    // TODO A different version
                }
            }
        }
        return null;
    }
}
