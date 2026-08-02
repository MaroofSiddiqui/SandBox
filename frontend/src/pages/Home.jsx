import {
  ArrowRight,
  CheckCircle2,
  ClipboardCheck,
  LockKeyhole,
  ShieldCheck,
  UserCheck,
  Users,
} from "lucide-react";

import { useNavigate } from "react-router-dom";
import "./Home.css";

function Home() {
  const navigate = useNavigate();

  return (
    <main className="home-page">

      {/* =========================
          NAVBAR
         ========================= */}

      <nav className="home-navbar">

        <button
          className="home-brand"
          onClick={() => navigate("/")}
        >
          <div className="home-brand-icon">
            <ShieldCheck size={22} />
          </div>

          <span>
            Sand<span>Box</span>
          </span>
        </button>


        <div className="home-nav-links">

          <button
            className="home-nav-link active"
            onClick={() => navigate("/")}
          >
            Home
          </button>

          <button
            className="home-nav-link"
            onClick={() => navigate("/about")}
          >
            About
          </button>

        </div>


        <div className="home-nav-actions">

          <button
            className="home-login-button"
            onClick={() => navigate("/login")}
          >
            Sign In
          </button>

          <button
            className="home-register-button"
            onClick={() => navigate("/register")}
          >
            Create Account
          </button>

        </div>

      </nav>


      {/* =========================
          HERO
         ========================= */}

      <section className="home-hero">

        <div className="home-hero-content">

          <div className="home-hero-badge">
            <ShieldCheck size={15} />

            Secure Recruitment Platform
          </div>


          <h1>
            Smarter hiring starts with
            <span> structured evaluation.</span>
          </h1>


          <p className="home-hero-description">
            SandBox provides organizations with a secure,
            centralized platform to manage recruitment,
            evaluate candidates and make better hiring
            decisions.
          </p>


          <div className="home-hero-actions">

            <button
              className="home-primary-action"
              onClick={() => navigate("/register")}
            >
              Get Started

              <ArrowRight size={18} />
            </button>

            <button
              className="home-secondary-action"
              onClick={() => navigate("/about")}
            >
              Learn More
            </button>

          </div>


          <div className="home-hero-trust">

            <span>
              <CheckCircle2 size={15} />
              Secure access
            </span>

            <span>
              <CheckCircle2 size={15} />
              Role-based control
            </span>

            <span>
              <CheckCircle2 size={15} />
              Structured hiring
            </span>

          </div>

        </div>


        {/* =========================
            HERO VISUAL
           ========================= */}

        <div className="home-hero-visual">

          <div className="home-dashboard-preview">

            <div className="preview-header">

              <div>
                <p>Recruitment Overview</p>
                <span>Organization Workspace</span>
              </div>

              <div className="preview-status">
                Active
              </div>

            </div>


            <div className="preview-stats">

              <div className="preview-stat">
                <div className="preview-stat-icon">
                  <Users size={20} />
                </div>

                <div>
                  <span>Candidates</span>
                  <strong>248</strong>
                </div>
              </div>


              <div className="preview-stat">
                <div className="preview-stat-icon">
                  <ClipboardCheck size={20} />
                </div>

                <div>
                  <span>Evaluations</span>
                  <strong>86</strong>
                </div>
              </div>

            </div>


            <div className="preview-section">

              <div className="preview-section-heading">
                Recent Candidates
              </div>

              <CandidatePreview
                initials="AS"
                name="Aarav Sharma"
                status="Evaluated"
              />

              <CandidatePreview
                initials="SK"
                name="Sara Khan"
                status="In Review"
              />

              <CandidatePreview
                initials="RM"
                name="Rohan Mehta"
                status="Shortlisted"
              />

            </div>

          </div>

        </div>

      </section>


      {/* =========================
          FEATURES
         ========================= */}

      <section className="home-features">

        <div className="home-section-heading">

          <p>BUILT FOR MODERN RECRUITMENT</p>

          <h2>
            Everything needed for a secure
            hiring workflow
          </h2>

          <span>
            Manage recruitment through one structured,
            role-based platform.
          </span>

        </div>


        <div className="home-feature-grid">

          <FeatureCard
            icon={<LockKeyhole size={23} />}
            title="Secure Authentication"
            description="Protected authentication with email verification, secure password recovery and account protection."
          />

          <FeatureCard
            icon={<Users size={23} />}
            title="Role-Based Access"
            description="Separate workspaces and permissions for administrators, HR teams and candidates."
          />

          <FeatureCard
            icon={<ClipboardCheck size={23} />}
            title="Structured Evaluation"
            description="Create consistent candidate evaluation workflows for more organized hiring decisions."
          />

          <FeatureCard
            icon={<UserCheck size={23} />}
            title="Candidate Management"
            description="Keep candidate information and recruitment activity organized throughout the hiring process."
          />

        </div>

      </section>


      {/* =========================
          CTA
         ========================= */}

      <section className="home-cta">

        <div>
          <p>READY TO GET STARTED?</p>

          <h2>
            Build a better recruitment workflow.
          </h2>

          <span>
            Create your SandBox account and access
            the recruitment platform.
          </span>
        </div>

        <button onClick={() => navigate("/register")}>
          Create Account
          <ArrowRight size={18} />
        </button>

      </section>


      {/* =========================
          FOOTER
         ========================= */}

      <footer className="home-footer">

        <div className="home-footer-brand">

          <ShieldCheck size={18} />

          <span>SandBox</span>

        </div>

        <p>
          Secure Recruitment Management Platform
        </p>

        <span>
          © 2026 SandBox. All rights reserved.
        </span>

      </footer>

    </main>
  );
}


function FeatureCard({
  icon,
  title,
  description,
}) {
  return (
    <article className="home-feature-card">

      <div className="home-feature-icon">
        {icon}
      </div>

      <h3>{title}</h3>

      <p>{description}</p>

    </article>
  );
}


function CandidatePreview({
  initials,
  name,
  status,
}) {
  return (
    <div className="preview-candidate">

      <div className="preview-avatar">
        {initials}
      </div>

      <div className="preview-candidate-info">
        <strong>{name}</strong>
        <span>Software Developer</span>
      </div>

      <div className="preview-candidate-status">
        {status}
      </div>

    </div>
  );
}

export default Home;