export const NodeService = {
  getTreeNodesData() {
    return [
      {
        key: '0',
        label: '인사부',
        data: '인사부',
        icon: 'pi pi-fw pi-inbox',
        children: [
          {
            key: '0-0',
            label: '채용팀',
            data: '채용팀',
            icon: 'pi pi-fw pi-cog',
            children: [
              { key: '0-0-0', label: '홍길동', icon: 'pi pi-fw pi-user', data: 'Employee 홍길동' },
              { key: '0-0-1', label: '김철수', icon: 'pi pi-fw pi-user', data: 'Employee 김철수' }
            ]
          },
          {
            key: '0-1',
            label: '급여팀',
            data: '급여팀',
            icon: 'pi pi-fw pi-dollar',
            children: [
              { key: '0-1-0', label: '이영희', icon: 'pi pi-fw pi-user', data: 'Employee 이영희' },
              { key: '0-1-1', label: '박영수', icon: 'pi pi-fw pi-user', data: 'Employee 박영수' }
            ]
          }
        ]
      },
      {
        key: '1',
        label: 'IT부서',
        data: 'IT부서',
        icon: 'pi pi-fw pi-desktop',
        children: [
          {
            key: '1-0',
            label: '개발팀',
            data: '개발팀',
            icon: 'pi pi-fw pi-code',
            children: [
              { key: '1-0-0', label: '박민수', icon: 'pi pi-fw pi-user', data: 'Employee 박민수' },
              { key: '1-0-1', label: '최지연', icon: 'pi pi-fw pi-user', data: 'Employee 최지연' }
            ]
          }
        ]
      }
    ];
  },

  getTreeNodes() {
    return Promise.resolve(this.getTreeNodesData());
  }
};
