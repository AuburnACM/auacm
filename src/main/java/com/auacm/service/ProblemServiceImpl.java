package com.auacm.service;

import com.auacm.api.model.*;
import com.auacm.database.dao.ProblemDao;
import com.auacm.database.dao.ProblemDataDao;
import com.auacm.database.dao.SampleCaseDao;
import com.auacm.database.model.*;
import com.auacm.database.model.SampleCase;
import com.auacm.exception.ProblemNotFoundException;
import com.auacm.util.JsonUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

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

    public ProblemServiceImpl() {}

    @Override
    public Problem createProblem(CreateProblem problem) {
        if (!fileSystemService.doesFileExist("data/problems/")) {
            fileSystemService.createFolder("data/problems/");
        }
        if (problem.getImportZip() == null || problem.getImportZip().isEmpty()) {
            Problem newProblem = new Problem();
            newProblem.setName(problem.getName());
            newProblem.setAdded(System.currentTimeMillis() / 1000);
            if (problem.getAppearedIn() != null) {
                Competition competition = competitionService.getCompetitionByName(problem.getAppearedIn());
                if (competition != null) {
                    newProblem.setAppeared(problem.getAppearedIn());
                    newProblem.setCompetitionId(competition.getCid());
                } else {
                    newProblem.setAppeared("");
                }
            } else {
                newProblem.setAppeared("");
            }
            newProblem.setShortName(getShortName(problem.getName()));
            if (problem.getDifficulty() != null) {
                newProblem.setDifficulty(problem.getDifficulty() + "");
            } else {
                newProblem.setDifficulty("0");
            }
            ProblemData newData = new ProblemData();
            newData.setDescription(problem.getDescription());
            newData.setInputDescription(problem.getInputDesc());
            newData.setOutputDescription(problem.getOutputDesc());
            if (problem.getTimeLimit() != null) {
                newData.setTimeLimit(problem.getTimeLimit());
            } else {
                // TODO Run script and get time limit
                // Setting the default time limit to 2 seconds
                newData.setTimeLimit(2);
            }

            Problem finalProblem = problemDao.save(newProblem);
            newData.setPid(finalProblem.getPid());
            problemDataDao.save(newData);
            finalProblem.setProblemData(newData);

            ArrayList<SampleCase> sampleCases = new ArrayList<>();
            for (com.auacm.api.model.SampleCase sampleCase : problem.getSampleCaseList()) {
                SampleCase newSampleCase = new SampleCase();
                newSampleCase.getSampleCasePK().setCaseNum(sampleCase.getCaseNum());
                newSampleCase.setInput(sampleCase.getInput());
                newSampleCase.setOutput(sampleCase.getOutput());
                sampleCases.add(newSampleCase);
                newSampleCase.getSampleCasePK().setPid(finalProblem.getPid());
            }

            sampleCaseDao.save(sampleCases);
            newProblem.setSampleCases(sampleCases);


            if (problem.getInputZip() != null) {
                fileSystemService.saveProblemInputZip(finalProblem.getPid() + "", problem.getInputZip());
            }

            if (problem.getOutputZip() != null) {
                fileSystemService.saveProblemOutputZip(finalProblem.getPid() + "", problem.getOutputZip());
            }

            if (problem.getInputFiles() != null) {
                int index = 1;
                for (MultipartFile file : problem.getInputFiles()) {
                    fileSystemService.saveProblemInputFile(finalProblem.getPid() + "", file, String.format("in%d.txt", index));
                    index++;
                }
            }

            if (problem.getOutputFiles() != null) {
                int index = 1;
                for (MultipartFile file : problem.getOutputFiles()) {
                    fileSystemService.saveProblemOutputFile(finalProblem.getPid() + "", file, String.format("out%d.txt", index));
                    index++;
                }
            }

            fileSystemService.saveSolutionFile(finalProblem.getPid() + "", problem.getSolutionFile());
            fileSystemService.createProblemZip(finalProblem.getPid() + "", jsonUtil.toJsonObject(getProblemResponse(finalProblem)));
            return finalProblem;
        } else {
            return saveProblemZip(problem);
        }
    }

    private String getShortName(String name) {
        String shortName = name.toLowerCase().replaceAll(" ", "");
        int index = 0;
        try {
            Problem problem = problemDao.findByShortNameIgnoreCase(shortName);
            while (problem != null) {
                index++;
                problem = problemDao.findByShortNameIgnoreCase(shortName+ index);
            }
            return shortName + (index > 0 ? index : "");
        } catch (ProblemNotFoundException e) {
            return shortName + (index > 0 ? index : "");
        }
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
    public Problem updateProblem(String identifier, CreateProblem problem) {
        Problem problem1 = getProblem(identifier);
        if (problem.getImportZip() == null || problem.getImportZip().isEmpty()) {
            if (problem.getAppearedIn() != null) {
                Competition competition = competitionService.getCompetitionByName(problem.getAppearedIn());
                if (competition != null) {
                    problem1.setAppeared(problem.getAppearedIn());
                    problem1.setCompetitionId(competition.getCid());
                } else {
                    problem1.setAppeared("");
                }
            } else {
                problem1.setAppeared("");
            }
            if (problem.getName() != null) {
                problem1.setName(problem.getName());
                problem1.setShortName(getShortName(problem.getName()));
            }
            if (problem.getDifficulty() != null) {
                problem1.setDifficulty(problem.getDifficulty() + "");
            }
            problemDao.save(problem1);

            // Update problem data
            ProblemData problemData = problemDataDao.findOne(problem1.getPid());
            if (problem.getDescription() != null) {
                problemData.setDescription(problem.getDescription());
            }
            if (problem.getInputDesc() != null) {
                problemData.setInputDescription(problem.getInputDesc());
            }
            if (problem.getOutputDesc() != null) {
                problemData.setOutputDescription(problem.getOutputDesc());
            }
            if (problem.getTimeLimit() != null) {
                problemData.setTimeLimit(problem.getTimeLimit());
            } else {
                // TODO Run script and get time limit
                // Setting the default time limit to 2 seconds
                problemData.setTimeLimit(2);
            }
            problemDataDao.save(problemData);

            ArrayList<SampleCase> sampleCases = new ArrayList<>();
            if (problem.getSampleCaseList() != null) {
                sampleCaseDao.deleteAllBySampleCasePK_Pid(problem1.getPid());
                for (com.auacm.api.model.SampleCase sampleCase : problem.getSampleCaseList()) {
                    SampleCase newSampleCase = new SampleCase();
                    newSampleCase.getSampleCasePK().setCaseNum(sampleCase.getCaseNum());
                    newSampleCase.setInput(sampleCase.getInput());
                    newSampleCase.setOutput(sampleCase.getOutput());
                    sampleCases.add(newSampleCase);
                    newSampleCase.getSampleCasePK().setPid(problem1.getPid());
                }
                sampleCaseDao.save(sampleCases);
                problem1.setSampleCases(sampleCases);
            } else {
                problem1.setSampleCases(sampleCaseDao.findAllBySampleCasePK_Pid(problem1.getPid()));
            }

            if (problem.getInputZip() != null) {
                fileSystemService.saveProblemInputZip(problem1.getPid() + "", problem.getInputZip());
            }

            if (problem.getOutputZip() != null) {
                fileSystemService.saveProblemOutputZip(problem1.getPid() + "", problem.getOutputZip());
            }

            if (problem.getInputFiles() != null) {
                int index = 1;
                for (MultipartFile file : problem.getInputFiles()) {
                    fileSystemService.saveProblemInputFile(problem1.getPid() + "", file, String.format("in%d.txt", index));
                    index++;
                }
            }

            if (problem.getOutputFiles() != null) {
                int index = 1;
                for (MultipartFile file : problem.getOutputFiles()) {
                    fileSystemService.saveProblemOutputFile(problem1.getPid() + "", file, String.format("out%d.txt", index));
                    index++;
                }
            }
            if (problem.getSolutionFile() != null) {
                fileSystemService.saveSolutionFile(problem1.getPid() + "", problem.getSolutionFile());
            }
            problem1.setProblemData(problemData);
        } else {
            // TODO Import the zip
        }
        fileSystemService.createProblemZip(problem1.getPid() + "", jsonUtil.toJsonObject(getProblemResponse(problem1)));
        return problem1;
    }

    @Override
    @Transactional
    public void deleteProblem(Problem problem) {
        problemDao.delete(problem);
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
    public Problem getProblemForPid(long pid) {
        try {
            Problem problem = problemDao.findOne(pid);
            if (competitionService.isInUpcomingCompetition(problem)) {
                if (SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                        .contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
                    return problem;
                } else {
                    throw new ProblemNotFoundException("Failed to find a problem for pid " + pid + ".");
                }
            } else {
                return problem;
            }
        } catch (JpaObjectRetrievalFailureException e) {
            throw new ProblemNotFoundException("Failed to find a problem for pid " + pid + ".");
        }
    }

    @Override
    public Problem getProblemForShortName(String shortName) {
        try {
            Problem problem = problemDao.findByShortNameIgnoreCase(shortName);
            if (competitionService.isInUpcomingCompetition(problem)) {
                if (SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                        .contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
                    return problem;
                } else {
                    throw new ProblemNotFoundException("Failed to find a problem for shortname " + shortName + ".");
                }
            } else {
                return problem;
            }
        } catch (JpaObjectRetrievalFailureException e) {
            throw new ProblemNotFoundException("Failed to find a problem for shortname " + shortName + ".");
        }
    }

    @Override
    public com.auacm.api.proto.Problem.ProblemListWrapper getProblemListResponse(List<Problem> problems) {
        com.auacm.api.proto.Problem.ProblemListWrapper.Builder builder = com.auacm.api.proto.Problem.ProblemListWrapper.newBuilder();
        for (Problem problem : problems) {
            builder.addData(com.auacm.api.proto.Problem.SimpleProblemResponse.newBuilder()
                    .setAdded(problem.getAdded())
                    .setAppeared(problem.getAppeared())
                    .setCompRelease(problem.getCompetitionId())
                    .setDifficuty(problem.getDifficulty())
                    .setName(problem.getName())
                    .setPid(problem.getPid())
                    .setShortName(problem.getShortName())
                    .setSolved(solvedProblemService.hasSolved(problem))
                    .setTimeLimit(problem.getProblemData().getTimeLimit())
                    .setUrl(String.format("/problems/%s/info.pdf", problem.getShortName())));
        }
        return builder.build();
    }

    @Override
    public com.auacm.api.proto.Problem.ProblemWrapper getProblemResponse(Problem problem) {
        com.auacm.api.proto.Problem.ProblemResponse.Builder builder = com.auacm.api.proto.Problem.ProblemResponse.newBuilder();
        builder.setAdded(problem.getAdded())
                .setAppeared(problem.getAppeared())
                .setCompRelease(problem.getCompetitionId())
                .setDescription(problem.getProblemData().getDescription())
                .setDifficulty(problem.getDifficulty())
                .setInputDesc(problem.getProblemData().getInputDescription())
                .setName(problem.getName())
                .setOutputDesc(problem.getProblemData().getOutputDescription())
                .setPid(problem.getPid())
                .setShortName(problem.getShortName())
                .setTimeLimit(problem.getProblemData().getTimeLimit());
        for (SampleCase sampleCase : problem.getSampleCases()) {
            builder.addSampleCases(com.auacm.api.proto.Problem.SampleCase.newBuilder()
                    .setCaseNum(sampleCase.getSampleCasePK().getCaseNum())
                    .setInput(sampleCase.getInput())
                    .setOutput(sampleCase.getOutput()));
        }
        return com.auacm.api.proto.Problem.ProblemWrapper.newBuilder().setData(builder).build();
    }

    private Problem saveProblemZip(CreateProblem problem) {
        long currentTime = System.currentTimeMillis();
        if (fileSystemService.saveTempFile(problem.getImportZip(),
                currentTime + "", problem.getImportZip().getOriginalFilename())) {
            if (fileSystemService.unzipFile(String.format("%s%s/%s", fileSystemService.getTmpFolder(),
                    currentTime + "", problem.getImportZip().getOriginalFilename()))) {
                String data = fileSystemService.getFileContents(String.format("%s%s/data.json",
                        fileSystemService.getTmpFolder(), currentTime + ""));
                if (data != null) {
                    JsonObject object = new JsonParser().parse(data).getAsJsonObject();
                    if (object.get("exportVersion").getAsInt() == 1) {
                        JsonObject dataObject = object.get("data").getAsJsonObject();
                        Problem newProblem = new Problem();
                        newProblem.setName(dataObject.get("name").getAsString());
                        newProblem.setShortName(getShortName(dataObject.get("name").getAsString()));
                        newProblem.setAdded(System.currentTimeMillis() / 1000);
                        if (dataObject.has("appearedIn")) {
                            Competition competition = competitionService.getCompetitionByName(dataObject.get("appearedIn").getAsString());
                            if (competition != null) {
                                newProblem.setAppeared(dataObject.get("appearedIn").getAsString());
                                newProblem.setCompetitionId(competition.getCid());
                            } else {
                                newProblem.setAppeared("");
                            }
                        } else {
                            newProblem.setAppeared("");
                        }
                        newProblem.setDifficulty(dataObject.get("difficulty").getAsString());
                        Problem savedProblem = problemDao.save(newProblem);
                        ProblemData data1 = new ProblemData();
                        data1.setDescription(dataObject.get("description").getAsString());
                        data1.setInputDescription(dataObject.get("inputDesc").getAsString());
                        data1.setOutputDescription(dataObject.get("outputDesc").getAsString());
                        data1.setTimeLimit(dataObject.get("timeLimit").getAsInt());
                        data1.setPid(savedProblem.getPid());
                        problemDataDao.save(data1);
                        newProblem.setProblemData(data1);
                        ArrayList<SampleCase> sampleCases = new ArrayList<>();
                        if (dataObject.has("sampleCases")) {
                            for (JsonElement e : dataObject.get("sampleCases").getAsJsonArray()) {
                                JsonObject obj = e.getAsJsonObject();
                                SampleCase sampleCase = new SampleCase();
                                sampleCase.setSampleCasePK(new SampleCasePK(savedProblem.getPid(), obj.get("caseNum").getAsLong()));
                                sampleCase.setInput(obj.get("input").getAsString());
                                sampleCase.setOutput(obj.get("output").getAsString());
                                sampleCases.add(sampleCase);
                            }
                            savedProblem.setSampleCases(sampleCases);
                            sampleCaseDao.save(sampleCases);
                        } else {
                            savedProblem.setSampleCases(new ArrayList<>());
                        }
                        fileSystemService.moveFile(String.format("%s%s/in/", fileSystemService.getTmpFolder(),
                                currentTime + ""), String.format("data/problems/%d/in/", savedProblem.getPid()));
                        fileSystemService.moveFile(String.format("%s%s/out/", fileSystemService.getTmpFolder(),
                                currentTime + ""), String.format("data/problems/%d/out/", savedProblem.getPid()));
                        fileSystemService.moveFile(String.format("%s%s/test/", fileSystemService.getTmpFolder(),
                                currentTime + ""), String.format("data/problems/%d/test/", savedProblem.getPid()));
                        fileSystemService.createProblemZip(savedProblem.getPid() + "",
                                jsonUtil.toJsonObject(getProblemResponse(savedProblem)));
                        fileSystemService.deleteFile(String.format("%s%s", fileSystemService.getTmpFolder(), currentTime + ""));
                        return savedProblem;
                    } else {
                        // TODO A different version
                    }
                } else {
                    // TODO Something?
                }
            }
        }
        return null;
    }
}
