import React from 'react';
import { ChevronRight } from 'lucide-react';

const HomeChoice = ({ text, onClick }) => {
    return (
        <button
            onClick={onClick}
            className="w-64 h-32 bg-white rounded-xl shadow-lg hover:shadow-xl transition-all duration-300 transform hover:scale-105 border border-gray-200 hover:border-blue-300"
        >
            <div className="flex items-center justify-center h-full px-6">
        <span className="text-lg font-semibold text-gray-700 text-center">
          {text}
        </span>
                <ChevronRight className="ml-2 w-5 h-5 text-gray-500" />
            </div>
        </button>
    );
};

export default HomeChoice;