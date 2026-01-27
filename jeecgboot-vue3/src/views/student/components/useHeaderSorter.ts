import { BasicColumn } from '/@/components/Table';

/**
 * @param columns 列配置
 * @param dictMap 字典映射
 * @returns 包含处理后的列配置
 */
export function useColumnSorter(columns: BasicColumn[]) {
  const processedColumns: BasicColumn[] = columns.map((col) => {
    // 克隆列配置对象来避免直接修改原始对象
    const newCol = { ...col };

    // 激活排序功能
    newCol.sorter = true;

    return newCol;
  });

  return {
    columns: processedColumns,
  };
}
