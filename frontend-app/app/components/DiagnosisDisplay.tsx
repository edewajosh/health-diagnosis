// components/DiagnosisDisplay.tsx
import { DiagnosisResult } from '@/app/page';

interface DiagnosisDisplayProps {
  diagnosis: DiagnosisResult[];
}

export function DiagnosisDisplay({ diagnosis }: DiagnosisDisplayProps) {
  return (
    <div className="space-y-8">
      <h2 className="text-3xl font-bold text-gray-900 text-center mb-6">
        Preliminary Diagnostic Suggestions
      </h2>

      <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
        {diagnosis.map((item, index) => (
          <div
            key={index}
            className="bg-white border border-gray-200 rounded-xl shadow-md overflow-hidden hover:shadow-lg transition-shadow"
          >
            <div className="p-6">
              <div className="flex justify-between items-start mb-4">
                <h3 className="text-xl font-semibold text-gray-900">
                  {item.issue.name}
                </h3>
                <span className="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-indigo-100 text-indigo-800">
                  {item.issue.accuracy}%
                </span>
              </div>

              <div className="text-sm text-gray-600 mb-4">
                <span className="font-medium">ICD:</span> {item.issue.icd}
                <br />
                <span className="font-medium">Professional name:</span> {item.issue.profName}
              </div>

              <div className="mt-4">
                <h4 className="text-sm font-medium text-gray-700 mb-2">Recommended Specialist(s):</h4>
                <ul className="space-y-1 text-sm text-gray-600">
                  {item.specialisation.map((spec, i) => (
                    <li key={i} className="flex items-center">
                      <svg className="w-4 h-4 mr-2 text-indigo-600" fill="currentColor" viewBox="0 0 20 20">
                        <path d="M8 9a3 3 0 100-6 3 3 0 000 6zM8 11a6 6 0 016 6H2a6 6 0 016-6zM16 7a1 1 0 10-2 0v1h-1a1 1 0 100 2h1v1a1 1 0 102 0v-1h1a1 1 0 100-2h-1V7z" />
                      </svg>
                      {spec.specialistName} ({spec.name})
                    </li>
                  ))}
                </ul>
              </div>
            </div>
          </div>
        ))}
      </div>

      <p className="text-center text-sm text-gray-500 mt-8 italic">
        These are preliminary suggestions only. Clinical correlation and professional medical evaluation are essential.
      </p>
    </div>
  );
}