import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";

const AddHall = () => {
  const navigate = useNavigate();
  const token = localStorage.getItem("token");

  const [formData, setFormData] = useState({
    name: "",
    lat: "",
    lng: "",
    basePrice: "",
    virtualTourUrl: "",
    amenities: [],
  });
  const [imageFile, setImageFile] = useState(null);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleImageChange = (e) => {
    setImageFile(e.target.files[0]);
  };

const handleSubmit = async () => {
  try {
    const payload = {
      name: formData.name,
      lat: parseFloat(formData.lat),
      lng: parseFloat(formData.lng),
      basePrice: parseFloat(formData.basePrice),
      virtualTourUrl: formData.virtualTourUrl,
      amenities: formData.amenities,
    };

    const response = await axios.post("http://localhost:8080/api/halls/create", payload, {
      headers: { Authorization: `Bearer ${token}` },
    });

    // Upload image
    if (imageFile) {
      const hallId = response.data.id; // Assuming your response contains ID
      const formDataImage = new FormData();
      formDataImage.append("image", imageFile);
      formDataImage.append("description", "Main hall image");

      await axios.post(
        `http://localhost:8080/api/halls/${hallId}/upload-image`,
        formDataImage,
        {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "multipart/form-data",
          },
        }
      );
    }

    alert("✅ Hall added successfully!");
    navigate("/vendor-dashboard/manage-halls");
  } catch (error) {
    console.error("Error saving hall:", error);
    alert("❌ Failed to add hall. Please try again.");
  }
};

  return (
    <div className="max-w-xl mx-auto mt-10 bg-white p-6 rounded shadow">
      <h2 className="text-xl font-bold mb-4">➕ Add New Hall</h2>

      <div className="mb-3">
        <label className="block mb-1 font-medium">Name</label>
        <input
          type="text"
          name="name"
          className="w-full border p-2 rounded"
          value={formData.name}
          onChange={handleInputChange}
        />
      </div>

      <div className="mb-3">
        <label className="block mb-1 font-medium">Latitude</label>
        <input
          type="number"
          step="any"
          name="lat"
          className="w-full border p-2 rounded"
          value={formData.lat}
          onChange={handleInputChange}
        />
      </div>

      <div className="mb-3">
        <label className="block mb-1 font-medium">Longitude</label>
        <input
          type="number"
          step="any"
          name="lng"
          className="w-full border p-2 rounded"
          value={formData.lng}
          onChange={handleInputChange}
        />
      </div>

      <div className="mb-3">
        <label className="block mb-1 font-medium">Base Price (₹)</label>
        <input
          type="number"
          name="basePrice"
          className="w-full border p-2 rounded"
          value={formData.basePrice}
          onChange={handleInputChange}
        />
      </div>

      <div className="mb-3">
        <label className="block mb-1 font-medium">Virtual Tour URL</label>
        <input
          type="text"
          name="virtualTourUrl"
          className="w-full border p-2 rounded"
          value={formData.virtualTourUrl}
          onChange={handleInputChange}
        />
      </div>

      <div className="mb-3">
        <label className="block mb-1 font-medium">Upload Image</label>
        <input type="file" accept="image/*" onChange={handleImageChange} />
      </div>

      <div className="flex justify-end gap-2 mt-4">
        <button
          className="bg-gray-300 text-gray-800 px-4 py-2 rounded"
          onClick={() => navigate("/vendor-dashboard/manage-halls")}
        >
          Cancel
        </button>
        <button
          className="bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700"
          onClick={handleSubmit}
        >
          Add Hall
        </button>
      </div>
    </div>
  );
};

export default AddHall;
