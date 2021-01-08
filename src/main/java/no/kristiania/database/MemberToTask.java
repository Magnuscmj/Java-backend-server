package no.kristiania.database;

public class MemberToTask extends IdEntity {
    private Integer id;
    private Integer taskId;
    private Integer memberId;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    @Override
    public Integer getTaskId() {
        return taskId;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }
}
