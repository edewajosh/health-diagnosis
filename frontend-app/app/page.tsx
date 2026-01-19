// app/page.tsx
'use client';

import { useState, useEffect } from 'react';
import axios from 'axios';
import Select from 'react-select';
import { DiagnosisDisplay } from './components/DiagnosisDisplay';
import { Button } from './components/ui/button';

interface SymptomOption {
  value: string;
  label: string;
}

interface DiagnosisIssue {
  id: string;
  name: string;
  accuracy: string;
  icd: string;
  profName: string;
}

interface Specialisation {
  id: string;
  name: string;
  specId: string;
  specialistName: string;
}

export interface DiagnosisResult {
  issue: DiagnosisIssue;
  specialisation: Specialisation[];
}
// interface ApiMedicSymptom {
//   id: string;
//   name: string;
// }

interface SymptomOption {
  value: string;
  label: string;
}

export default function Home() {
  const [symptomsList, setSymptomsList] = useState<SymptomOption[]>([]);
  const [selectedSymptoms, setSelectedSymptoms] = useState<SymptomOption[]>([]);
  const [patientName, setPatientName] = useState('');
  const [gender, setGender] = useState('male');
  const [yearOfBirth, setYearOfBirth] = useState('1990');
  const [diagnosis, setDiagnosis] = useState<DiagnosisResult[] | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
  axios
    .get('http://localhost:8080/api/v1/symptoms')
    .then((res) => {
      console.log(res.data);
      const symptoms = res.data

      const options: SymptomOption[] = symptoms.map((s) => ({
        value: s.id,
        label: s.name,
      }));

      setSymptomsList(options);
    })
    .catch((err) => {
      console.error('Failed to load symptoms:', err);
      setError('Could not load symptoms list');
    });
}, []);

  const handleSubmit = async () => {
    if (!patientName.trim()) {
      setError('Patient name is required');
      return;
    }
    if (selectedSymptoms.length === 0) {
      setError('Please select at least one symptom');
      return;
    }
    if (!yearOfBirth || Number(yearOfBirth) < 1900 || Number(yearOfBirth) > new Date().getFullYear()) {
      setError('Please enter a valid year of birth');
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const symptomsPayload = selectedSymptoms.map((sym) => ({
        id: sym.value,
        description: sym.label,
      }));

      const payload = {
        patientName: patientName.trim(),
        gender,
        yearOfBirth, // sent as string
        symptoms: symptomsPayload,
      };

      const res = await axios.post('http://localhost:8080/api/v1/diagnosis', payload, {
        headers: { 'Content-Type': 'application/json' },
      });

      setDiagnosis(res.data);
    } catch (err: Error | any) {
      setError(err.response?.data?.message || 'Failed to get diagnosis. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleValidation = async (isValid: boolean) => {
    if (!diagnosis) return;

    const result = {
      patientName,
      gender,
      yearOfBirth,
      symptoms: selectedSymptoms.map((s) => ({ id: s.value, description: s.label })),
      diagnosis: JSON.stringify(diagnosis), // or send as array
      isValid,
    };

    try {
      await axios.post('http://localhost:8080/api/v1/save', result);
      alert(`Diagnosis marked as ${isValid ? 'Valid' : 'Invalid'} and saved!`);
    } catch (err) {
      alert('Failed to save diagnosis');
    }
  };

  return (
    <div className="min-h-screen bg-linear-to-b from-gray-50 to-gray-100 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-4xl mx-auto">
        <div className="text-center mb-10">
          <h1 className="text-4xl md:text-5xl font-bold text-gray-900 tracking-tight">
            Community Health Diagnosis Tool
          </h1>
          <p className="mt-4 text-lg text-gray-600 max-w-2xl mx-auto">
            Enter patient information and symptoms to get preliminary diagnostic suggestions and specialist recommendations.
            <br />
            <span className="text-red-600 font-medium">For professional medical use only — always confirm with a doctor.</span>
          </p>
        </div>

        <div className="bg-white shadow-2xl rounded-2xl p-8 md:p-10 border border-gray-200">
          {error && (
            <div className="mb-8 p-4 bg-red-50 border border-red-200 text-red-700 rounded-xl flex items-center">
              <svg className="w-5 h-5 mr-2" fill="currentColor" viewBox="0 0 20 20">
                <path d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" />
              </svg>
              {error}
            </div>
          )}

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
            {/* Patient Name */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Patient Name <span className="text-red-600">*</span>
              </label>
              <input
                type="text"
                value={patientName}
                onChange={(e) => setPatientName(e.target.value)}
                placeholder="e.g. John Doe"
                className="block w-full rounded-lg border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm py-3 px-4"
              />
            </div>

            {/* Gender */}
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Gender</label>
              <select
                value={gender}
                onChange={(e) => setGender(e.target.value)}
                className="block w-full rounded-lg border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm py-3 px-4"
              >
                <option value="male">Male</option>
                <option value="female">Female</option>
              </select>
            </div>

            {/* Year of Birth */}
            <div className="md:col-span-2">
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Year of Birth
              </label>
              <input
                type="number"
                value={yearOfBirth}
                onChange={(e) => setYearOfBirth(e.target.value)}
                min={1900}
                max={new Date().getFullYear()}
                className="block w-full md:w-1/3 rounded-lg border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm py-3 px-4"
              />
            </div>
          </div>

          {/* Symptoms */}
          <div className="mb-8">
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Symptoms <span className="text-red-600">*</span>
            </label>
            <Select
              isMulti
              options={symptomsList}
              value={selectedSymptoms}
              onChange={(selected) => setSelectedSymptoms(selected as SymptomOption[])}
              className="react-select-container"
              classNamePrefix="react-select"
              placeholder="Search and select one or more symptoms..."
              noOptionsMessage={() => 'No symptoms found'}
            />
          </div>

          {/* Submit */}
          <Button
            onClick={handleSubmit}
            disabled={loading || selectedSymptoms.length === 0 || !patientName.trim()}
            isLoading={loading}
            fullWidth
          >
            Get Preliminary Diagnosis
          </Button>

          {diagnosis && (
            <div className="mt-14">
              <DiagnosisDisplay diagnosis={diagnosis} />
              <div className="mt-10 flex flex-col sm:flex-row gap-5 justify-center">
                <Button
                  variant="success"
                  onClick={() => handleValidation(true)}
                >
                  Valid Diagnosis ✓
                </Button>
                <Button
                  variant="danger"
                  onClick={() => handleValidation(false)}
                >
                  Invalid Diagnosis ✗
                </Button>
              </div>
            </div>
          )}
        </div>

        <footer className="mt-12 text-center text-sm text-gray-500">
          © {new Date().getFullYear()} Community Health Facility • Nairobi, Kenya
        </footer>
      </div>
    </div>
  );
}