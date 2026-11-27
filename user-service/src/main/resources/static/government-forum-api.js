// 政府论坛API扩展
class GovernmentForumAPI {

    // 发布官方帖子
    static async publishOfficialPost(postData) {
        const response = await fetch('http://localhost:8002/api/forum/official', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                title: postData.title,
                content: postData.content,
                type: postData.type,
                author: '政府官方',
                isOfficial: true
            })
        });
        return await response.json();
    }

    // 获取论坛统计数据
    static async getForumStats() {
        const response = await fetch('http://localhost:8002/api/forum/stats');
        return await response.json();
    }

    // 获取热点话题
    static async getHotTopics() {
        const response = await fetch('http://localhost:8002/api/forum/hot-topics');
        return await response.json();
    }

    // 删除违规帖子
    static async deletePost(postId) {
        const response = await fetch(`http://localhost:8002/api/forum/posts/${postId}`, {
            method: 'DELETE'
        });
        return await response.json();
    }

    // 新增：获取报告统计数据（连接到report-service）
    static async getReportStats() {
        const response = await fetch('http://localhost:8002/api/reports');
        const reports = await response.json();

        // 统计各类报告数量
        const stats = {
            totalReports: reports.length,
            pendingReports: reports.filter(r => r.status === '待处理').length,
            processingReports: reports.filter(r => r.status === '处理中').length,
            completedReports: reports.filter(r => r.status === '已完成').length
        };

        return stats;
    }

    // 新增：获取部门任务统计
    static async getDepartmentStats() {
        const response = await fetch('http://localhost:8002/api/reports');
        const reports = await response.json();

        const departmentStats = {};
        reports.forEach(report => {
            const dept = report.assignedDepartment || '未分配';
            departmentStats[dept] = (departmentStats[dept] || 0) + 1;
        });

        return departmentStats;
    }
}

// 论坛管理功能
class ForumManager {
    constructor() {
        this.stats = {};
        this.hotTopics = [];
        this.reportStats = {};
        this.departmentStats = {};
    }

    async init() {
        await this.loadStats();
        await this.loadHotTopics();
        await this.loadReportStats();
        await this.loadDepartmentStats();
    }

    async loadStats() {
        try {
            this.stats = await GovernmentForumAPI.getForumStats();
            this.updateStatsUI();
        } catch (error) {
            console.log('论坛统计API暂不可用，使用模拟数据');
            this.stats = { totalPosts: 156, activeUsers: 89, dailyPosts: 23, hotTopics: 5 };
            this.updateStatsUI();
        }
    }

    async loadHotTopics() {
        try {
            this.hotTopics = await GovernmentForumAPI.getHotTopics();
            this.updateHotTopicsUI();
        } catch (error) {
            console.log('热点话题API暂不可用，使用模拟数据');
            this.hotTopics = [
                { title: '城市交通优化建议' },
                { title: '环境保护措施讨论' },
                { title: '市政设施改进方案' }
            ];
            this.updateHotTopicsUI();
        }
    }

    async loadReportStats() {
        try {
            this.reportStats = await GovernmentForumAPI.getReportStats();
            this.updateReportStatsUI();
        } catch (error) {
            console.log('报告统计API暂不可用');
            this.reportStats = { totalReports: 45, pendingReports: 12, processingReports: 8, completedReports: 25 };
            this.updateReportStatsUI();
        }
    }

    async loadDepartmentStats() {
        try {
            this.departmentStats = await GovernmentForumAPI.getDepartmentStats();
            this.updateDepartmentStatsUI();
        } catch (error) {
            console.log('部门统计API暂不可用');
            this.departmentStats = { '交通管理局': 15, '环境保护局': 8, '市政设施局': 12, '城管执法局': 6, '公安消防局': 4 };
            this.updateDepartmentStatsUI();
        }
    }

    updateStatsUI() {
        // 更新论坛统计UI
        document.querySelectorAll('.forum-stats .stat-number').forEach((element, index) => {
            const values = Object.values(this.stats);
            if (values[index] !== undefined) {
                element.textContent = values[index];
            }
        });
    }

    updateHotTopicsUI() {
        const container = document.querySelector('.hot-topics-list');
        if (container) {
            container.innerHTML = this.hotTopics.map(topic =>
                `<li style="padding:8px 0; border-bottom:1px solid #eee;">${topic.title}</li>`
            ).join('');
        }
    }

    updateReportStatsUI() {
        // 更新报告统计UI
        const reportStatsElement = document.querySelector('.report-stats');
        if (reportStatsElement) {
            reportStatsElement.innerHTML = `
                <div>总报告数: ${this.reportStats.totalReports || 0}</div>
                <div>待处理: ${this.reportStats.pendingReports || 0}</div>
                <div>处理中: ${this.reportStats.processingReports || 0}</div>
                <div>已完成: ${this.reportStats.completedReports || 0}</div>
            `;
        }
    }

    updateDepartmentStatsUI() {
        // 更新部门统计UI
        const deptStatsElement = document.querySelector('.department-stats');
        if (deptStatsElement) {
            deptStatsElement.innerHTML = Object.entries(this.departmentStats)
                .map(([dept, count]) => `<div>${dept}: ${count}个任务</div>`)
                .join('');
        }
    }
}

// 初始化论坛管理
document.addEventListener('DOMContentLoaded', function() {
    window.forumManager = new ForumManager();
    window.forumManager.init();
});