// Full vendor data by city
const vendorDataByCity = {
  Delhi: [
    {
      name: "Raj Bridal Studio",
      type: "Bridal Wear",
      image: "https://tse1.mm.bing.net/th/id/OIP.qX_PqjkUfDXU8jx8bHfPIAHaHa?pid=Api&P=0&h=220",
      rating: 4.5
    },
    {
      name: "Elite Caterers",
      type: "Catering Services",
      image: "https://img.freepik.com/premium-photo/buffet-food-catering-food-party-made-by-aiartificial-intelligence_41969-12071.jpg?w=2000",
      rating: 4.3
    },
    {
      name: "LensPro Photography",
      type: "Photography",
      image: "https://cdn.pixabay.com/video/2023/05/31/165234-832460016_tiny.jpg",
      rating: 4.7
    },
    {
      name: "Amaltas Couture",
      type: "Bridal Wear",
      image: "https://wallpapers.com/images/hd/bride-pictures-0wbxnmaixub5z87o.jpg",
      rating: 5.0
    },
    {
      name: "Tek Chand Arjit Goel",
      type: "Bridal Wear",
      image: "https://static.malabargoldanddiamonds.com/media/boiimages/bg/thumbs/1682603269Punjabi-Bride-Mobile.png",
      rating: 4.9
    },
    {
      name: "Chef & Beyond",
      type: "Catering Services",
      image: "https://tse2.mm.bing.net/th/id/OIP.-qHTBQeWSpLgLOfsN8lEQwHaE7?pid=Api&P=0&h=220",
      rating: 4.9
    },
    {
      name: "Elegant Cards",
      type: "Invitations",
      image: "https://tse4.mm.bing.net/th/id/OIP.NyjFaoRdrB9VzfsunLd5XwHaEK?pid=Api&P=0&h=220",
      rating: 4.9
    },
    {
      name: "Raju Mehandi Art",
      type: "Mehandi Artist",
      image: "https://png.pngtree.com/thumb_back/fh260/background/20220325/pngtree-mehandi-decoration-india-henna-photo-image_3818389.jpg",
      rating: 4.9
    }
  ],
  Mumbai: [
    {
      name: "Bombay Grooms",
      type: "Groom Wear",
      image: "https://images.pexels.com/videos/15116132/groom-15116132.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500",
      rating: 4.4
    },
    {
      name: "ShutterSpeed Studio",
      type: "Photography",
      image: "https://clippingpathstudio.com/wp-content/uploads/2021/03/Faster-Shutter-Speed.jpg",
      rating: 4.6
    },
    {
      name: "Tandoori Nights",
      type: "Catering Services",
      image: "https://tse1.mm.bing.net/th/id/OIP.Xkmzez6u196HK7mUGmLlZQHaE5?pid=Api&P=0&h=220",
      rating: 4.2
    },
    {
      name: "The Paper Story",
      type: "Invitations",
      image: "https://tse3.mm.bing.net/th/id/OIP.C7ZkEste2DNACYq3S0aheAHaEw?pid=Api&P=0&h=220",
      rating: 4.7
    },
    {
      name: "Henna by Simran",
      type: "Mehandi Artist",
      image: "https://i.pinimg.com/originals/03/b5/2f/03b52fd1a42de84578bb415da5256daf.jpg",
      rating: 4.8
    }
  ],
  Bangalore: [
    {
      name: "PixelStory Studio",
      type: "Photography",
      image: "https://images.pexels.com/photos/2253870/pexels-photo-2253870.jpeg",
      rating: 4.8
    },
    {
      name: "Food Fiesta",
      type: "Catering Services",
      image: "https://tse1.mm.bing.net/th/id/OIP.j3VeMoPG_JlMODcCBm_7LgHaE8?pid=Api&P=0&h=220",
      rating: 4.6
    }
  ],
  Hyderabad: [
    {
      name: "Wedding Frames",
      type: "Photography",
      image: "https://images.pexels.com/photos/931162/pexels-photo-931162.jpeg",
      rating: 4.6
    }
  ],
  Chennai: [
    {
      name: "Chennai Bridal Studio",
      type: "Bridal Wear",
      image: "https://tse4.mm.bing.net/th/id/OIP.yYx4qEgPUnuIoAKd7IeorQHaE7?pid=Api&P=0&h=220",
      rating: 4.6
    },
    {
      name: "South Spice Caterers",
      type: "Catering Services",
      image: "https://tse2.mm.bing.net/th/id/OIP.u0UQQmb9GYiTwZpqDVPAagHaEo?pid=Api&P=0&h=220",
      rating: 4.5
    },
    {
      name: "Frame Makers",
      type: "Photography",
      image: "https://tse3.mm.bing.net/th/id/OIP.XDGKy_tZP6dYk_e-Asm4mwHaE8?pid=Api&P=0&h=220",
      rating: 4.7
    }
  ],
  Goa: [
    {
      name: "Beach Vibes Photography",
      type: "Photography",
      image: "https://tse1.mm.bing.net/th/id/OIP.Bi3CiDP72Dy9PxvEJ_OtFQHaEK?pid=Api&P=0&h=220",
      rating: 4.8
    },
    {
      name: "Goan Delights",
      type: "Catering Services",
      image: "https://tse1.mm.bing.net/th/id/OIP.MkbpmddVr1MT6uxF4X8ktQHaHa?pid=Api&P=0&h=220",
      rating: 4.6
    },
    {
      name: "Sunset Mehandi",
      type: "Mehandi Artist",
      image: "https://hindi.cdn.zeenews.com/hindi/sites/default/files/2022/07/09/1214627-mehdi3.jpg?im=FitAndFill=(1200,900)",
      rating: 4.7
    }
  ],
  Jaipur: [
    {
      name: "Royal Rajputana Wear",
      type: "Groom Wear",
      image: "https://rare-gallery.com/uploads/posts/551520-adult-boutonniere.jpg",
      rating: 4.9
    },
    {
      name: "Pink City Invitations",
      type: "Invitations",
      image: "https://tse2.mm.bing.net/th/id/OIP.ne6MjBW8Kr2lgleWplI8IQHaE7?pid=Api&P=0&h=220",
      rating: 4.5
    },
    {
      name: "Jaipur Moments",
      type: "Photography",
      image: "https://wallpaperaccess.com/full/3148602.jpg",
      rating: 4.7
    }
  ],
  Pune: [
    {
      name: "Pune Glamour Studio",
      type: "Bridal Wear",
      image: "https://tse2.mm.bing.net/th/id/OIP.NYNfowUBd_OpA26tRGFJBAHaE8?pid=Api&P=0&h=220",
      rating: 4.6
    },
    {
      name: "Tandoor Trails",
      type: "Catering Services",
      image: "https://tse2.mm.bing.net/th/id/OIP.u0UQQmb9GYiTwZpqDVPAagHaEo?pid=Api&P=0&h=220",
      rating: 4.4
    },
    {
      name: "Snapshot Studio",
      type: "Photography",
      image: "https://tse4.mm.bing.net/th/id/OIP.slnKiwRUVow1TL0A2MmsHAHaE8?pid=Api&P=0&h=220",
      rating: 4.5
    }
  ],
  Kolkata: [
    {
      name: "Kolkata Couture",
      type: "Bridal Wear",
      image: "https://tse1.mm.bing.net/th/id/OIP.XTZEUKlG9I8qUPDnZkfmkgHaE8?pid=Api&P=0&h=220",
      rating: 4.7
    },
    {
      name: "Bengal Biryani Caterers",
      type: "Catering Services",
      image: "https://tse4.mm.bing.net/th/id/OIP.t4T7mAR9_ePyNQnk1nOBpwHaEK?pid=Api&P=0&h=220",
      rating: 4.6
    },
    {
      name: "Heritage Frames",
      type: "Photography",
      image: "https://tse2.mm.bing.net/th/id/OIP.Nk6iwt_GvO9SjfJqh4zadQHaHa?pid=Api&P=0&h=220",
      rating: 4.8
    }
  ],
  Lucknow: [
    {
      name: "Nawabi Styles",
      type: "Groom Wear",
      image: "https://wallpaperaccess.com/full/1448061.jpg",
      rating: 4.8
    },
    {
      name: "Lucknowi Invitations",
      type: "Invitations",
      image: "https://tse1.mm.bing.net/th/id/OIP.DRtT5jeGBzLz2xQSAPkUgAAAAA?pid=Api&P=0&h=220",
      rating: 4.7
    },
    {
      name: "Awadh Moments",
      type: "Photography",
      image: "https://tse4.mm.bing.net/th/id/OIP.8Cynh6Fxc1qGdMUuI-0JCgAAAA?pid=Api&P=0&h=220",
      rating: 4.6
    }
  ]
};

// Named exports for category-based views
export const bridalWearData = vendorDataByCity.Delhi.filter(v => v.type === 'Bridal Wear')
  .concat(vendorDataByCity.Chennai?.filter(v => v.type === 'Bridal Wear') || [])
  .concat(vendorDataByCity.Pune?.filter(v => v.type === 'Bridal Wear') || [])
  .concat(vendorDataByCity.Kolkata?.filter(v => v.type === 'Bridal Wear') || []);

export const photographyData = Object.values(vendorDataByCity).flat().filter(v => v.type === 'Photography');
export const invitationsData = Object.values(vendorDataByCity).flat().filter(v => v.type === 'Invitations');
export const groomWearData = Object.values(vendorDataByCity).flat().filter(v => v.type === 'Groom Wear');
export const cateringData = Object.values(vendorDataByCity).flat().filter(v => v.type === 'Catering Services');
export const mehandiData = Object.values(vendorDataByCity).flat().filter(v => v.type === 'Mehandi Artist');

export default vendorDataByCity;