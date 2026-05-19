export function exportToCSV(filename, headers, rows) {
  const bom = '﻿' // UTF-8 BOM for Excel compatibility
  const csvContent = [
    headers.join(','),
    ...rows.map(row => headers.map(h => {
      const val = (row[h] ?? '').toString()
      return val.includes(',') || val.includes('"') ? `"${val.replace(/"/g, '""')}"` : val
    }).join(','))
  ].join('\n')

  const blob = new Blob([bom + csvContent], { type: 'text/csv;charset=utf-8;' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = filename
  link.click()
  URL.revokeObjectURL(url)
}
